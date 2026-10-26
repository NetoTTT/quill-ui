"""Gera a capa 512x512 do Quill UI (pixel art 128x128, upscale 4x)."""

import math
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[2]
OUT = ROOT / "docs" / "art" / "quill_ui_cover_512.png"
PREVIEW = ROOT / "build" / "art-preview" / "quill_ui_cover_512.png"

N = 128
BAYER = (
    (0, 8, 2, 10),
    (12, 4, 14, 6),
    (3, 11, 1, 9),
    (15, 7, 13, 5),
)

BG_TOP = (0x07, 0x0A, 0x0E)
BG_BOT = (0x0E, 0x15, 0x1D)
GRID_SOFT = (0x12, 0x1A, 0x23)
GRID_HARD = (0x1A, 0x24, 0x30)
RULER = (0x20, 0x2B, 0x38)

PANEL_BG = (0x0E, 0x12, 0x17)
PANEL_BORDER = (0x2B, 0x34, 0x40)
PANEL_SHADOW = (0x00, 0x00, 0x00)
HEADER_BG = (0x0A, 0x0E, 0x14)
DIVIDER = (0x24, 0x2C, 0x36)

TITLE = (0xF2, 0xF5, 0xF8)
TEXT = (0xC6, 0xD0, 0xDA)
TEXT_SOFT = (0xA9, 0xB5, 0xC1)
MUTED = (0x7F, 0x8B, 0x99)
MUTED_DARK = (0x5F, 0x6B, 0x78)

ACCENT = (0xE0, 0xA9, 0x3B)
ACCENT_HOVER = (0xF3, 0xC5, 0x5E)
ACCENT_DARK = (0xC9, 0x8E, 0x2C)
ACCENT_DEEP = (0x8A, 0x64, 0x20)
PRIMARY_BG = (0x24, 0x1C, 0x11)

DANGER = (0xE0, 0x6A, 0x6A)
SUCCESS = (0x65, 0xD3, 0x9A)
INFO = (0x7F, 0xB4, 0xF5)

SURFACE = (0x15, 0x1C, 0x25)
SURFACE_ALT = (0x1A, 0x22, 0x2C)
SURFACE_BORDER = (0x26, 0x31, 0x3D)
SURFACE_HEADER = (0x10, 0x16, 0x1E)
CHIP_BG = (0x1B, 0x24, 0x2E)
CHIP_BORDER = (0x33, 0x40, 0x4E)
SELECTED_BG = (0x2A, 0x31, 0x40)
SELECTED_BORDER = (0x3A, 0x46, 0x54)
BUTTON_BORDER = (0x35, 0x41, 0x50)

NIB_DARK = (0x3A, 0x41, 0x4B)
NIB_LIGHT = (0x5A, 0x64, 0x72)
NIB_EDGE = (0x1A, 0x1F, 0x26)
SHAFT_LIGHT = (0xFF, 0xE0, 0xA0)

FONT5 = {
    "A": (".###.", "#...#", "#...#", "#####", "#...#", "#...#", "#...#"),
    "B": ("####.", "#...#", "#...#", "####.", "#...#", "#...#", "####."),
    "C": (".###.", "#...#", "#....", "#....", "#....", "#...#", ".###."),
    "D": ("####.", "#...#", "#...#", "#...#", "#...#", "#...#", "####."),
    "E": ("#####", "#....", "#....", "####.", "#....", "#....", "#####"),
    "F": ("#####", "#....", "#....", "####.", "#....", "#....", "#...."),
    "G": (".###.", "#...#", "#....", "#.###", "#...#", "#...#", ".###."),
    "H": ("#...#", "#...#", "#...#", "#####", "#...#", "#...#", "#...#"),
    "I": ("#####", "..#..", "..#..", "..#..", "..#..", "..#..", "#####"),
    "J": ("..###", "...#.", "...#.", "...#.", "...#.", "#..#.", ".##.."),
    "K": ("#...#", "#..#.", "#.#..", "##...", "#.#..", "#..#.", "#...#"),
    "L": ("#....", "#....", "#....", "#....", "#....", "#....", "#####"),
    "M": ("#...#", "##.##", "#.#.#", "#.#.#", "#...#", "#...#", "#...#"),
    "N": ("#...#", "##..#", "#.#.#", "#..##", "#...#", "#...#", "#...#"),
    "O": (".###.", "#...#", "#...#", "#...#", "#...#", "#...#", ".###."),
    "P": ("####.", "#...#", "#...#", "####.", "#....", "#....", "#...."),
    "Q": (".###.", "#...#", "#...#", "#...#", "#.#.#", "#..#.", ".##.#"),
    "R": ("####.", "#...#", "#...#", "####.", "#.#..", "#..#.", "#...#"),
    "S": (".####", "#....", "#....", ".###.", "....#", "....#", "####."),
    "T": ("#####", "..#..", "..#..", "..#..", "..#..", "..#..", "..#.."),
    "U": ("#...#", "#...#", "#...#", "#...#", "#...#", "#...#", ".###."),
    "V": ("#...#", "#...#", "#...#", "#...#", "#...#", ".#.#.", "..#.."),
    "W": ("#...#", "#...#", "#...#", "#.#.#", "#.#.#", "##.##", "#...#"),
    "X": ("#...#", "#...#", ".#.#.", "..#..", ".#.#.", "#...#", "#...#"),
    "Y": ("#...#", "#...#", ".#.#.", "..#..", "..#..", "..#..", "..#.."),
    "Z": ("#####", "....#", "...#.", "..#..", ".#...", "#....", "#####"),
    " ": ("...", "...", "...", "...", "...", "...", "..."),
}


def blend(c1, c2, t):
    return tuple(int(c1[i] + (c2[i] - c1[i]) * t) for i in range(3))


def dither(t, x, y, levels=7):
    v = t * levels
    i = int(v)
    if (v - i) * 16 > BAYER[y % 4][x % 4] + 0.5:
        i += 1
    return min(i, levels) / levels


def text_cells(text, scale, spacing=1):
    cells = set()
    cx = 0
    for ch in text:
        pat = FONT5[ch]
        width = len(pat[0])
        for gy, row in enumerate(pat):
            for gx, val in enumerate(row):
                if val == "#":
                    for yy in range(scale):
                        for xx in range(scale):
                            cells.add((cx + gx * scale + xx, gy * scale + yy))
        cx += (width + spacing) * scale
    return cells, cx - spacing * scale, 7 * scale


def draw_text(img, text, x, y, scale, rows, outline=None):
    cells, _, _ = text_cells(text, scale)
    if outline is not None:
        ring = set()
        for cx, cy in cells:
            for dx in (-1, 0, 1):
                for dy in (-1, 0, 1):
                    p = (cx + dx, cy + dy)
                    if p not in cells:
                        ring.add(p)
        for cx, cy in ring:
            px, py = x + cx, y + cy
            if 0 <= px < N and 0 <= py < N:
                img.putpixel((px, py), outline + (255,))
    for cx, cy in cells:
        px, py = x + cx, y + cy
        if 0 <= px < N and 0 <= py < N:
            row = min(cy // scale, len(rows) - 1)
            img.putpixel((px, py), rows[row] + (255,))


def paint_background(img):
    px = img.load()
    for y in range(N):
        c = blend(BG_TOP, BG_BOT, y / (N - 1))
        for x in range(N):
            px[x, y] = c + (255,)

    for x in range(0, N, 8):
        for y in range(N):
            px[x, y] = blend(px[x, y][:3], GRID_SOFT, 0.6) + (255,)
    for y in range(0, N, 8):
        for x in range(N):
            px[x, y] = blend(px[x, y][:3], GRID_SOFT, 0.6) + (255,)
    for x in range(0, N, 32):
        for y in range(N):
            px[x, y] = blend(px[x, y][:3], GRID_HARD, 0.55) + (255,)
    for y in range(0, N, 32):
        for x in range(N):
            px[x, y] = blend(px[x, y][:3], GRID_HARD, 0.55) + (255,)

    for x in range(N):
        for y in range(0, 4):
            px[x, y] = blend(px[x, y][:3], (0x0A, 0x0E, 0x14), 0.8) + (255,)
    for y in range(N):
        for x in range(0, 4):
            px[x, y] = blend(px[x, y][:3], (0x0A, 0x0E, 0x14), 0.8) + (255,)
    for x in range(0, N, 8):
        px[x, 3] = blend(px[x, 3][:3], RULER, 0.9) + (255,)
        px[3, x] = blend(px[3, x][:3], RULER, 0.9) + (255,)
    for x in range(0, N, 32):
        for y in (2, 3):
            px[x, y] = blend(px[x, y][:3], RULER, 0.9) + (255,)
        for y in (2, 3):
            px[y, x] = blend(px[y, x][:3], RULER, 0.9) + (255,)

    for cx, cy, radius, strength, color in (
        (102, 42, 40, 0.40, (0x6B, 0x46, 0x1F)),
        (44, 54, 46, 0.30, (0x14, 0x22, 0x30)),
    ):
        for y in range(N):
            for x in range(N):
                d = ((x - cx) ** 2 + (y - cy) ** 2) ** 0.5
                if d >= radius:
                    continue
                t = (1 - d / radius) ** 1.7 * strength
                t = dither(t, x, y)
                if t > 0:
                    px[x, y] = blend(px[x, y][:3], color, t) + (255,)

    for y in range(N):
        for x in range(N):
            dx = (x - 62) / 84
            dy = (y - 62) / 90
            d = (dx * dx + dy * dy) ** 0.5
            t = max(0.0, d - 0.6) * 0.95
            if t > 0:
                t = dither(min(t, 1.0), x, y, 5) * 0.5
                px[x, y] = blend(px[x, y][:3], (0, 0, 0), t) + (255,)


def ghost_frame(img, x0, y0, x1, y1, color):
    px = img.load()
    for x in range(x0, x1 + 1):
        px[x, y0] = color + (255,)
        px[x, y1] = color + (255,)
    for y in range(y0, y1 + 1):
        px[x0, y] = color + (255,)
        px[x1, y] = color + (255,)


def draw_palette_strip(img):
    swatches = (ACCENT, INFO, SUCCESS, DANGER)
    for i, c in enumerate(swatches):
        x = 10 + i * 5
        for yy in range(6, 12):
            for xx in range(x, x + 4):
                img.putpixel((xx, yy), c + (255,))
        img.putpixel((x, 6), blend(c, (255, 255, 255), 0.35) + (255,))
    draw_text(img, "THEME", 10, 13, 1, (MUTED_DARK,) * 7)


def draw_panel(img):
    d = ImageDraw.Draw(img)

    d.rectangle((15, 25, 87, 85), fill=PANEL_SHADOW + (130,))
    d.rectangle((12, 22, 84, 82), fill=PANEL_BG + (255,), outline=PANEL_BORDER + (255,))
    d.rectangle((13, 23, 83, 30), fill=HEADER_BG + (255,))
    d.line((13, 31, 83, 31), fill=DIVIDER + (255,))

    d.rectangle((17, 25, 20, 28), fill=ACCENT + (255,))
    img.putpixel((18, 26), TITLE + (255,))
    draw_text(img, "QUILL", 24, 24, 1, (TITLE,) * 7)
    draw_text(img, "UI", 58, 24, 1, (ACCENT,) * 7)
    for x in (74, 77, 80):
        d.rectangle((x, 26, x + 1, 27), fill=BUTTON_BORDER + (255,))

    d.rectangle((14, 32, 47, 37), fill=SURFACE + (255,))
    draw_text(img, "THEME", 16, 32, 1, (TITLE,) * 7)
    draw_text(img, "GRID", 54, 32, 1, (MUTED,) * 7)
    d.line((14, 39, 47, 39), fill=ACCENT + (255,))
    d.line((13, 41, 83, 41), fill=(0x1F, 0x28, 0x33, 255))

    rows = (
        (42, INFO, MUTED, MUTED_DARK, False),
        (51, ACCENT, TITLE, TEXT_SOFT, True),
        (60, SUCCESS, MUTED, MUTED_DARK, False),
    )
    for y, icon, line1, line2, selected in rows:
        bg = SELECTED_BG if selected else SURFACE
        border = SELECTED_BORDER if selected else SURFACE_BORDER
        d.rectangle((14, y, 56, y + 7), fill=bg + (255,), outline=border + (255,))
        if selected:
            d.rectangle((14, y, 15, y + 7), fill=ACCENT + (255,))
            ix, tx = 17, 23
        else:
            ix, tx = 16, 22
        d.rectangle((ix, y + 2, ix + 3, y + 5), fill=icon + (255,))
        d.rectangle((ix + 1, y + 3, ix + 2, y + 4), fill=blend(icon, (255, 255, 255), 0.45) + (255,))
        d.line((tx, y + 2, tx + 15, y + 2), fill=line1 + (255,))
        d.line((tx, y + 5, tx + 9, y + 5), fill=line2 + (255,))
        d.rectangle((43, y + 2, 54, y + 5), fill=CHIP_BG + (255,), outline=(ACCENT_DEEP if selected else CHIP_BORDER) + (255,))
        d.line((45, y + 3, 52, y + 3), fill=(ACCENT if selected else MUTED_DARK) + (255,))

    d.rectangle((60, 42, 82, 66), fill=SURFACE + (255,), outline=SURFACE_BORDER + (255,))
    d.rectangle((61, 43, 81, 45), fill=SURFACE_HEADER + (255,))
    for x in (64, 67):
        d.line((x, 44, x, 44), fill=(0x3A, 0x46, 0x54, 255))
    swatches = ((63, 48, ACCENT), (72, 48, INFO), (63, 55, SUCCESS), (72, 55, DANGER))
    for sx, sy, c in swatches:
        d.rectangle((sx, sy, sx + 7, sy + 5), fill=c + (255,), outline=(0x0A, 0x0E, 0x14, 255))
        d.line((sx + 1, sy + 1, sx + 6, sy + 1), fill=blend(c, (255, 255, 255), 0.35) + (255,))
    d.line((63, 62, 79, 62), fill=(0x2B, 0x34, 0x40, 255))
    d.line((63, 64, 73, 64), fill=(0x1F, 0x28, 0x33, 255))

    d.rectangle((14, 70, 82, 78), fill=ACCENT + (255,))
    d.line((14, 70, 82, 70), fill=ACCENT_HOVER + (255,))
    d.line((14, 78, 82, 78), fill=ACCENT_DARK + (255,))
    d.line((82, 70, 82, 78), fill=ACCENT_DARK + (255,))
    draw_text(img, "APPLY", 34, 71, 1, (PRIMARY_BG,) * 7)


def draw_quill(img):
    base = (84.0, 70.0)
    tip = (118.0, 8.0)
    dx, dy = tip[0] - base[0], tip[1] - base[1]
    length = math.hypot(dx, dy)
    ux, uy = dx / length, dy / length
    pxv, pyv = -uy, ux

    for i in range(int(length * 0.13), int(length * 0.995), 2):
        f = (i - length * 0.13) / (length * 0.87)
        w = 11.5 * math.sin(math.pi * max(0.0, min(1.0, f))) ** 0.72
        cx = base[0] + ux * i
        cy = base[1] + uy * i
        for side in (-1, 1):
            for j in range(1, max(2, int(w)) + 1):
                lean = 0.5 * j
                x = round(cx + pxv * side * j + ux * lean)
                y = round(cy + pyv * side * j + uy * lean)
                if not (0 <= x < N and 0 <= y < N):
                    continue
                if j >= int(w):
                    col = ACCENT_HOVER
                elif (i // 2 + (side + 1)) % 6 == 0:
                    col = ACCENT_DARK
                else:
                    col = ACCENT
                img.putpixel((x, y), col + (255,))

    for i in range(int(length * 0.10), int(length) + 1):
        x = round(base[0] + ux * i)
        y = round(base[1] + uy * i)
        if 0 <= x < N and 0 <= y < N:
            img.putpixel((x, y), SHAFT_LIGHT + (255,))
            sx = round(x + pxv)
            sy = round(y + pyv)
            if 0 <= sx < N and 0 <= sy < N:
                img.putpixel((sx, sy), ACCENT_DARK + (255,))

    for k in range(0, 8):
        x = base[0] - ux * k
        y = base[1] - uy * k
        w = 3 if k < 3 else (2 if k < 6 else 1)
        for off in range(-w, w + 1):
            px = round(x + pxv * off)
            py = round(y + pyv * off)
            if 0 <= px < N and 0 <= py < N:
                img.putpixel((px, py), (NIB_EDGE if off in (-w, w) else NIB_DARK) + (255,))
        if k in (1, 2, 3, 4, 5):
            img.putpixel((round(x), round(y)), NIB_LIGHT + (255,))
    for k in (6, 7):
        for off in (0,):
            px = round(base[0] - ux * k + pxv * off)
            py = round(base[1] - uy * k + pyv * off)
            if 0 <= px < N and 0 <= py < N:
                img.putpixel((px, py), ACCENT_HOVER + (255,))

    d = ImageDraw.Draw(img)
    d.line((round(base[0] - pxv * 2), round(base[1] - pyv * 2), round(base[0] + pxv * 2), round(base[1] + pyv * 2)), fill=ACCENT_HOVER + (255,))

    for x, y, c in (
        (110, 21, ACCENT_HOVER),
        (114, 38, ACCENT),
        (92, 58, ACCENT_HOVER),
        (96, 29, ACCENT),
        (88, 75, ACCENT_HOVER),
        (108, 52, ACCENT_DARK),
    ):
        img.putpixel((x, y), c + (255,))
    for cx, cy in ((112, 30), (90, 66)):
        img.putpixel((cx, cy), ACCENT_HOVER + (255,))
        for dx2, dy2 in ((1, 0), (-1, 0), (0, 1), (0, -1)):
            img.putpixel((cx + dx2, cy + dy2), ACCENT + (255,))


def draw_title(img):
    d = ImageDraw.Draw(img)
    d.rectangle((0, 100, N - 1, N - 1), fill=(0x08, 0x0B, 0x0F, 255))
    d.line((0, 100, N - 1, 100), fill=(0x1B, 0x24, 0x2E, 255))
    d.line((0, 101, N - 1, 101), fill=(0x10, 0x16, 0x1E, 255))

    light = (TITLE, TITLE, (0xE4, 0xEB, 0xF2), (0xD6, 0xDF, 0xE8), TEXT, TEXT_SOFT, MUTED)
    gold = (ACCENT_HOVER, ACCENT_HOVER, ACCENT, ACCENT, ACCENT_DARK, ACCENT_DARK, ACCENT_DEEP)
    draw_text(img, "QUILL", 19, 103, 2, light, (0x04, 0x06, 0x0A))
    draw_text(img, "UI", 87, 103, 2, gold, (0x04, 0x06, 0x0A))

    draw_text(img, "CLIENT", 9, 120, 1, (MUTED,) * 7)
    draw_text(img, "UI", 49, 120, 1, (ACCENT_DARK,) * 7)
    draw_text(img, "FRAMEWORK", 65, 120, 1, (MUTED,) * 7)


def main():
    img = Image.new("RGBA", (N, N), (0, 0, 0, 0))
    paint_background(img)

    ghost_frame(img, 18, 22, 90, 82, (0x19, 0x22, 0x2C))
    ghost_frame(img, 8, 28, 80, 88, (0x16, 0x1E, 0x27))

    draw_palette_strip(img)
    draw_panel(img)
    draw_quill(img)
    draw_title(img)

    big = img.resize((512, 512), Image.NEAREST)
    OUT.parent.mkdir(parents=True, exist_ok=True)
    PREVIEW.parent.mkdir(parents=True, exist_ok=True)
    big.convert("RGB").save(OUT)
    big.convert("RGB").save(PREVIEW)
    print("cover:", OUT)
    print("preview:", PREVIEW)


if __name__ == "__main__":
    main()
