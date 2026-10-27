package com.quillui.text;

import com.quillui.render.QuillGradient;
import com.quillui.theme.QuillThemeData;

/**
 * Declarative text layout and paint settings. {@code maxWidth <= 0} means no wrapping and
 * {@code maxLines <= 0} means unlimited lines.
 */
public record QuillTextStyle(
		int color,
		QuillGradient gradient,
		boolean shadow,
		Align align,
		int maxWidth,
		int maxLines,
		boolean ellipsis
) {
	public enum Align {
		LEFT,
		CENTER,
		RIGHT
	}

	public QuillTextStyle {
		if (align == null) {
			align = Align.LEFT;
		}
		maxWidth = Math.max(0, maxWidth);
		maxLines = Math.max(0, maxLines);
	}

	public static final QuillTextStyle DEFAULT = new QuillTextStyle(
			QuillThemeData.DEFAULT.color(QuillThemeData.ColorRole.TEXT),
			null,
			false,
			Align.LEFT,
			0,
			0,
			false
	);

	public static QuillTextStyle of(QuillThemeData theme, QuillThemeData.ColorRole role) {
		return of((theme == null ? QuillThemeData.DEFAULT : theme).color(role));
	}

	public static QuillTextStyle of(int color) {
		return new QuillTextStyle(color, null, false, Align.LEFT, 0, 0, false);
	}

	public static QuillTextStyle gradient(QuillGradient gradient) {
		return new QuillTextStyle(
				gradient == null ? DEFAULT.color : gradient.startColor(),
				gradient,
				false,
				Align.LEFT,
				0,
				0,
				false
		);
	}

	public QuillTextStyle withColor(int color) {
		return new QuillTextStyle(color, gradient, shadow, align, maxWidth, maxLines, ellipsis);
	}

	public QuillTextStyle withColor(QuillThemeData theme, QuillThemeData.ColorRole role) {
		return withColor((theme == null ? QuillThemeData.DEFAULT : theme).color(role));
	}

	public QuillTextStyle withGradient(QuillGradient gradient) {
		return new QuillTextStyle(
				gradient == null ? color : gradient.startColor(),
				gradient,
				shadow,
				align,
				maxWidth,
				maxLines,
				ellipsis
		);
	}

	public QuillTextStyle withShadow(boolean shadow) {
		return new QuillTextStyle(color, gradient, shadow, align, maxWidth, maxLines, ellipsis);
	}

	public QuillTextStyle withAlign(Align align) {
		return new QuillTextStyle(color, gradient, shadow, align, maxWidth, maxLines, ellipsis);
	}

	public QuillTextStyle withMaxWidth(int maxWidth) {
		return new QuillTextStyle(color, gradient, shadow, align, maxWidth, maxLines, ellipsis);
	}

	public QuillTextStyle withMaxLines(int maxLines) {
		return new QuillTextStyle(color, gradient, shadow, align, maxWidth, maxLines, ellipsis);
	}

	public QuillTextStyle withEllipsis(boolean ellipsis) {
		return new QuillTextStyle(color, gradient, shadow, align, maxWidth, maxLines, ellipsis);
	}
}
