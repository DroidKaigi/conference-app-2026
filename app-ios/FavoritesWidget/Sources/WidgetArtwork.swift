import SwiftUI

/// A stroke-only drawing taken from an Android vector drawable, tinted by its call site.
struct WidgetArtwork {
    let viewport: CGSize
    let strokeWidth: CGFloat
    let strokes: [String]
    var fills: [String] = []
}

struct WidgetArtworkView: View {
    let artwork: WidgetArtwork
    let size: CGSize
    let color: Color

    var body: some View {
        let scale = min(size.width / artwork.viewport.width, size.height / artwork.viewport.height)
        Canvas { context, _ in
            for data in artwork.strokes {
                context.stroke(
                    Path(vectorPathData: data).applying(CGAffineTransform(scaleX: scale, y: scale)),
                    with: .color(color),
                    style: StrokeStyle(
                        lineWidth: artwork.strokeWidth * scale,
                        lineCap: .round,
                        lineJoin: .miter,
                        miterLimit: 4
                    )
                )
            }
            for data in artwork.fills {
                let path = Path(vectorPathData: data).applying(CGAffineTransform(scaleX: scale, y: scale))
                context.fill(path, with: .color(color))
                context.stroke(
                    path,
                    with: .color(color),
                    style: StrokeStyle(lineWidth: artwork.strokeWidth * scale, lineCap: .round, lineJoin: .round)
                )
            }
        }
        .frame(width: size.width, height: size.height)
    }
}

struct SymbolMark: View {
    let size: CGFloat
    let color: Color

    var body: some View {
        WidgetArtworkView(
            artwork: WidgetArtworks.symbolMark,
            size: CGSize(width: size, height: size),
            color: color
        )
    }
}

struct Mascot: View {
    let artwork: WidgetArtwork
    let size: CGSize
    let color: Color

    var body: some View {
        WidgetArtworkView(artwork: artwork, size: size, color: color)
    }
}

private extension Path {
    /// Reads the `M`/`L`/`C`/`Z` subset of path data the widget artwork is drawn with.
    init(vectorPathData data: String) {
        self.init()
        var numbers: [CGFloat] = []
        var command: Character?
        for token in data.split(whereSeparator: \.isWhitespace) {
            if let value = Double(token) {
                numbers.append(CGFloat(value))
            } else {
                command = token.first
                numbers.removeAll()
                if command == "Z" { closeSubpath() }
                continue
            }
            switch command {
            case "M" where numbers.count == 2:
                move(to: CGPoint(x: numbers[0], y: numbers[1]))
                numbers.removeAll()
            case "L" where numbers.count == 2:
                addLine(to: CGPoint(x: numbers[0], y: numbers[1]))
                numbers.removeAll()
            case "C" where numbers.count == 6:
                addCurve(
                    to: CGPoint(x: numbers[4], y: numbers[5]),
                    control1: CGPoint(x: numbers[0], y: numbers[1]),
                    control2: CGPoint(x: numbers[2], y: numbers[3])
                )
                numbers.removeAll()
            default:
                break
            }
        }
    }
}
