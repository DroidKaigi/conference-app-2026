"""Builds the font binaries bundled in core/designsystem.

The design sets Courier Prime for the display voice and Noto Sans for everything else,
but neither family carries Japanese glyphs, and Compose Multiplatform offers no API to
direct per-glyph fallback to a bundled font (the skiko targets consult system fonts
first). The fallback is therefore resolved inside the binaries: each bundled font merges
the design's Latin face with Noto Sans JP, Latin glyphs winning where coverage overlaps.

All three inputs are licensed under the SIL Open Font License 1.1. The merged fonts are
renamed because a Modified Version must not use a Reserved Font Name (Noto Sans JP
reserves "Source").

Usage:
    python3 -m venv .venv && .venv/bin/pip install fonttools==4.63.0
    .venv/bin/python scripts/build-fonts.py
"""

import io
import os
import sys
import urllib.request
from pathlib import Path

# fontTools stamps save time into the head table; pin it so reruns are byte-identical.
os.environ["SOURCE_DATE_EPOCH"] = "0"

from fontTools import subset
from fontTools.merge import Merger
from fontTools.ttLib import TTFont
from fontTools.ttLib.scaleUpem import scale_upem
from fontTools.varLib.instancer import instantiateVariableFont

GOOGLE_FONTS_COMMIT = "e1118da94a8cb00cf6d06cdac9ef13eb1e5c6ab7"
SOURCE_BASE = f"https://raw.githubusercontent.com/google/fonts/{GOOGLE_FONTS_COMMIT}/ofl"
SOURCES = {
    "courier_prime": f"{SOURCE_BASE}/courierprime/CourierPrime-Regular.ttf",
    "noto_sans": f"{SOURCE_BASE}/notosans/NotoSans%5Bwdth%2Cwght%5D.ttf",
    "noto_sans_jp": f"{SOURCE_BASE}/notosansjp/NotoSansJP%5Bwght%5D.ttf",
}

OUT_DIR = Path(__file__).parent.parent / "core/designsystem/src/commonMain/composeResources/font"

COPYRIGHT = (
    "Merged from Courier Prime (Copyright 2015 The Courier Prime Project Authors), "
    "Noto Sans (Copyright 2022 The Noto Project Authors), and "
    "Noto Sans JP (Copyright 2014-2021 Adobe, with Reserved Font Name 'Source'). "
    "Licensed under the SIL Open Font License, Version 1.1."
)
LICENSE_DESCRIPTION = "This Font Software is licensed under the SIL Open Font License, Version 1.1."
LICENSE_URL = "https://openfontlicense.org"


def download(url: str) -> TTFont:
    with urllib.request.urlopen(url) as response:
        return TTFont(io.BytesIO(response.read()))


def instance(font: TTFont, axes: dict) -> TTFont:
    instantiateVariableFont(font, axes, inplace=True)
    # fontTools.merge cannot handle leftover variation data on fully-pinned fonts. BASE
    # carries a VarStore of its own and only serves vertical layout, as do vhea/vmtx.
    for tag in ("STAT", "MVAR", "HVAR", "VVAR", "avar", "fvar", "gvar", "cvar", "BASE", "DSIG", "vhea", "vmtx"):
        if tag in font:
            del font[tag]
    return font


def dehint(font: TTFont) -> None:
    """Drops TrueType hinting; scale_upem does not scale instructions, so a scaled
    hinted font renders deformed glyphs at small sizes."""
    options = subset.Options()
    options.hinting = False
    options.notdef_outline = True
    options.name_IDs = ["*"]
    options.name_languages = ["*"]
    options.layout_features = ["*"]
    subsetter = subset.Subsetter(options=options)
    subsetter.populate(unicodes=font.getBestCmap().keys())
    subsetter.subset(font)


def rename(font: TTFont, family: str, subfamily: str) -> None:
    postscript_name = family.replace(" ", "") + "-" + subfamily.replace(" ", "")
    full_name = f"{family} {subfamily}"
    name = font["name"]
    name.names = []
    for name_id, value in {
        0: COPYRIGHT,
        1: family,
        2: subfamily,
        3: f"{full_name};DroidKaigi 2026",
        4: full_name,
        6: postscript_name,
        13: LICENSE_DESCRIPTION,
        14: LICENSE_URL,
    }.items():
        name.setName(value, name_id, 3, 1, 0x409)
        name.setName(value, name_id, 1, 0, 0)


def build(latin: TTFont, jp: TTFont, family: str, subfamily: str, out_name: str) -> None:
    target_upem = jp["head"].unitsPerEm
    if latin["head"].unitsPerEm != target_upem:
        dehint(latin)
        scale_upem(latin, target_upem)
    work_dir = OUT_DIR / ".work"
    work_dir.mkdir(parents=True, exist_ok=True)
    latin_path = work_dir / "latin.ttf"
    jp_path = work_dir / "jp.ttf"
    latin.save(latin_path)
    jp.save(jp_path)

    merged = Merger().merge([str(latin_path), str(jp_path)])
    # The GSUB that fontTools.merge emits makes the Android font parser reject the whole
    # font, falling back silently to the platform default; a re-serialization through
    # pyftsubset does not repair it. Horizontal UI text loses only optional substitutions
    # (fi ligatures and the like) — GPOS kerning and GDEF survive.
    del merged["GSUB"]
    rename(merged, family, subfamily)
    out_path = OUT_DIR / out_name
    merged.save(out_path)

    latin_path.unlink()
    jp_path.unlink()
    work_dir.rmdir()
    print(f"built {out_name}: {out_path.stat().st_size / 1024 / 1024:.2f} MB")


def main() -> None:
    courier = download(SOURCES["courier_prime"])
    noto_sans_vf = SOURCES["noto_sans"]
    noto_sans_jp_vf = SOURCES["noto_sans_jp"]

    build(
        latin=courier,
        jp=instance(download(noto_sans_jp_vf), {"wght": 400}),
        family="Kaigi Mono",
        subfamily="Regular",
        out_name="kaigi_mono_regular.ttf",
    )
    build(
        latin=instance(download(noto_sans_vf), {"wght": 400, "wdth": 100}),
        jp=instance(download(noto_sans_jp_vf), {"wght": 400}),
        family="Kaigi Sans",
        subfamily="Regular",
        out_name="kaigi_sans_regular.ttf",
    )
    build(
        latin=instance(download(noto_sans_vf), {"wght": 500, "wdth": 100}),
        jp=instance(download(noto_sans_jp_vf), {"wght": 500}),
        family="Kaigi Sans",
        subfamily="Medium",
        out_name="kaigi_sans_medium.ttf",
    )


if __name__ == "__main__":
    sys.exit(main())
