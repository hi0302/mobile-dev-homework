"""Build a clean source-only Android Studio submission archive."""

import argparse
import re
from pathlib import Path
from zipfile import ZIP_DEFLATED, ZipFile, ZipInfo


def main():
    project = Path(__file__).resolve().parents[1]
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--output", type=Path, default=project.parent / "IC03-submission.zip")
    args = parser.parse_args()
    output = args.output.resolve()
    if output.is_relative_to(project):
        parser.error("The archive must be outside the IC03 project")

    catalog = project / "app/src/main/java/com/example/ic03/PhotoCatalog.kt"
    slots = re.findall(r'Slot\("(\d+)"', catalog.read_text(encoding="utf-8"))
    resources = project / "app/src/main/res/drawable-nodpi"
    for slot in slots:
        for kind in ("small", "large"):
            if not list(resources.glob(f"{kind}_photo_{slot}.*")):
                parser.error(f"Missing {kind} image for catalog slot {slot}")

    excluded_directories = {"build", ".gradle", ".kotlin", ".idea", ".git", "photo-originals", "screenshots", "__pycache__"}
    entries = []
    for path in sorted(project.rglob("*")):
        relative = path.relative_to(project)
        if not path.is_file() or any(part in excluded_directories for part in relative.parts):
            continue
        if path.name == "local.properties" or path.suffix.lower() in {".log", ".pyc", ".zip", ".apk", ".aab"}:
            continue
        entries.append((path, "IC03/" + relative.as_posix()))

    output.parent.mkdir(parents=True, exist_ok=True)
    with ZipFile(output, "w", compression=ZIP_DEFLATED, compresslevel=6) as archive:
        for path, name in entries:
            info = ZipInfo.from_file(path, name)
            info.compress_type = ZIP_DEFLATED
            if path.name == "gradlew":
                info.create_system = 3
                info.external_attr = 0o100755 << 16
            archive.writestr(info, path.read_bytes())

    with ZipFile(output) as archive:
        assert archive.testzip() is None, "ZIP integrity check failed"
        names = archive.namelist()
        assert "IC03/gradle/wrapper/gradle-wrapper.jar" in names
        assert not any(name.endswith((".apk", ".aab")) for name in names), "Archive must contain source only"
        assert len(names) == len(set(names)), "Duplicate archive entries"
        for path, name in entries:
            assert archive.read(name) == path.read_bytes(), f"Archive content mismatch: {name}"
    print(f"Created {output}")
    print(f"{len(entries)} files; {len(slots)} photo pairs; {output.stat().st_size / 1048576:.2f} MiB")
    print("Verified ZIP integrity and every included file")


if __name__ == "__main__":
    main()
