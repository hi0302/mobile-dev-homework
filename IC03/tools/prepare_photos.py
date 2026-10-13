"""Create Android thumbnail/large pairs without changing the source photos.

Usage: python tools/prepare_photos.py --source photo-originals
Requires Pillow: python -m pip install Pillow
"""

import argparse
from pathlib import Path

from PIL import Image, ImageOps


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--source", type=Path, required=True)
    parser.add_argument("--start-slot", type=int, default=1)
    args = parser.parse_args()
    if args.start_slot < 1:
        parser.error("--start-slot must be positive")

    project = Path(__file__).resolve().parents[1]
    source = args.source.resolve()
    destination = project / "app/src/main/res/drawable-nodpi"
    photos = sorted(p for p in source.iterdir() if p.suffix.lower() in {".jpg", ".jpeg", ".png", ".webp"})
    if not photos:
        parser.error("No JPEG, PNG, or WebP source photos found")

    # Check the whole batch before writing, so existing photo pairs are never overwritten.
    for slot in range(args.start_slot, args.start_slot + len(photos)):
        for kind in ("small", "large"):
            if list(destination.glob(f"{kind}_photo_{slot:02d}.*")):
                parser.error(f"Photo slot {slot:02d} already exists; use an unused --start-slot")

    destination.mkdir(parents=True, exist_ok=True)
    for slot, original in enumerate(photos, args.start_slot):
        with Image.open(original) as image:
            # Apply camera orientation before resizing; preserve aspect ratio and all content.
            photo = ImageOps.exif_transpose(image).convert("RGB")
            for kind, limit, quality in (("small", 240, 85), ("large", 1600, 90)):
                copy = photo.copy()
                copy.thumbnail((limit, limit), Image.Resampling.LANCZOS)
                target = destination / f"{kind}_photo_{slot:02d}.jpg"
                copy.save(target, quality=quality, optimize=True)
                print(f"{original.name} -> {target.name}: {copy.width} x {copy.height}")


if __name__ == "__main__":
    main()
