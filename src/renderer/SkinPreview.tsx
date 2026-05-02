import React, { useEffect, useRef } from "react";

const MINECRAFT_SKIN_WIDTH = 64;

function isSupportedSkinSize(w: number, h: number): boolean {
  return w === MINECRAFT_SKIN_WIDTH && (h === 32 || h === 64);
}

export function describeSkinFit(w: number, h: number): { valid: boolean; label: string } {
  if (!Number.isFinite(w) || !Number.isFinite(h) || w <= 0 || h <= 0) {
    return { valid: false, label: "No image loaded." };
  }

  if (isSupportedSkinSize(w, h)) {
    return {
      valid: true,
      label: `Valid Minecraft skin size: ${w}\u00d7${h}px (Steve/Alex layout).`
    };
  }

  return {
    valid: false,
    label: `Not a standard skin: ${w}\u00d7${h}px. Use PNG at 64\u00d732 (classic) or 64\u00d764 (modern).`
  };
}

function drawPartNearest(
  ctx: CanvasRenderingContext2D,
  img: HTMLImageElement,
  sx: number,
  sy: number,
  sw: number,
  sh: number,
  dx: number,
  dy: number,
  dw: number,
  dh: number,
  mirrorX: boolean,
): void {
  ctx.save();
  ctx.imageSmoothingEnabled = false;

  if (mirrorX) {
    ctx.translate(dx + dw, dy);
    ctx.scale(-1, 1);
    ctx.drawImage(img, sx, sy, sw, sh, 0, 0, dw, dh);
  } else {
    ctx.drawImage(img, sx, sy, sw, sh, dx, dy, dw, dh);
  }

  ctx.restore();
}

function drawMiniFront(ctx: CanvasRenderingContext2D, img: HTMLImageElement): void {
  const h = img.naturalHeight;
  const w = img.naturalWidth;
  if (!isSupportedSkinSize(w, h)) {
    return;
  }

  ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
  ctx.fillStyle = "#0f1324";
  ctx.fillRect(0, 0, ctx.canvas.width, ctx.canvas.height);

  const s = h <= 32 ? 9 : 8;
  const cx = ctx.canvas.width / 2;

  let baseDrop = 138;
  if (h <= 32) {
    baseDrop += 42;
  }

  const hl = ctx.canvas.height - baseDrop;

  drawPartNearest(ctx, img, 8, 8, 8, 8, cx - 4 * s, hl - (12 + 15 + 8) * s, 8 * s, 8 * s, false);
  drawPartNearest(ctx, img, 20, 20, 8, 12, cx - 4 * s, hl - (12 + 15) * s, 8 * s, 12 * s, false);

  const armH = h <= 32 ? Math.min(12, h - 20) : 12;
  const armSy = 20;
  drawPartNearest(ctx, img, 44, armSy, 4, armH, cx + 4 * s, hl - (12 + armH) * s, 4 * s, armH * s, false);

  drawPartNearest(ctx, img, 44, armSy, 4, armH, cx - 8 * s, hl - (12 + armH) * s, 4 * s, armH * s, true);

  drawPartNearest(ctx, img, 4, 20, 4, 12, cx - 6 * s, hl - 12 * s, 4 * s, 12 * s, false);

  const leftLegSx = h >= 64 ? 20 : 4;
  const leftLegSy = h >= 64 ? 52 : 20;
  const mirrorLeftLeg = h < 64;
  drawPartNearest(ctx, img, leftLegSx, leftLegSy, 4, 12, cx + 2 * s, hl - 12 * s, 4 * s, 12 * s, mirrorLeftLeg);
}

type SkinPreviewPanelsProps = {
  objectUrl: string | null;
  naturalWidth: number;
  naturalHeight: number;
};

export function SkinPreviewPanels(props: SkinPreviewPanelsProps): JSX.Element {
  const textureRef = useRef<HTMLCanvasElement>(null);
  const miniRef = useRef<HTMLCanvasElement>(null);
  const { objectUrl, naturalWidth, naturalHeight } = props;
  const { valid, label } = describeSkinFit(naturalWidth, naturalHeight);

  useEffect(() => {
    const textureCanvas = textureRef.current;
    const miniCanvas = miniRef.current;
    if (!textureCanvas || !miniCanvas) {
      return;
    }

    if (!objectUrl || !valid) {
      const tCtx = textureCanvas.getContext("2d");
      if (tCtx) {
        textureCanvas.width = Math.max(1, MINECRAFT_SKIN_WIDTH * 3);
        textureCanvas.height = Math.max(1, 96);
        tCtx.clearRect(0, 0, textureCanvas.width, textureCanvas.height);
        tCtx.fillStyle = "#05070f";
        tCtx.fillRect(0, 0, textureCanvas.width, textureCanvas.height);
      }

      const mCtx = miniCanvas.getContext("2d");
      miniCanvas.width = 220;
      miniCanvas.height = 220;
      if (mCtx) {
        mCtx.clearRect(0, 0, miniCanvas.width, miniCanvas.height);
        mCtx.fillStyle = "#0f1324";
        mCtx.fillRect(0, 0, miniCanvas.width, miniCanvas.height);
      }
      return;
    }

    const img = new Image();
    let cancelled = false;

    img.onload = () => {
      if (cancelled) {
        return;
      }

      const tCtx = textureCanvas.getContext("2d");
      if (tCtx) {
        const maxW = 320;
        const scale = Math.min(1, maxW / img.naturalWidth);
        textureCanvas.width = Math.max(1, Math.round(img.naturalWidth * scale));
        textureCanvas.height = Math.max(1, Math.round(img.naturalHeight * scale));
        tCtx.imageSmoothingEnabled = false;
        tCtx.fillStyle = "#05070f";
        tCtx.fillRect(0, 0, textureCanvas.width, textureCanvas.height);
        tCtx.drawImage(img, 0, 0, img.naturalWidth, img.naturalHeight, 0, 0, textureCanvas.width, textureCanvas.height);

        const step = Math.max(textureCanvas.width, textureCanvas.height) / 64;
        tCtx.save();
        tCtx.strokeStyle = "rgba(255,255,255,0.05)";
        tCtx.lineWidth = 1;
        for (let x = 0; x <= textureCanvas.width; x += Math.max(1, step)) {
          tCtx.beginPath();
          tCtx.moveTo(x + 0.5, 0);
          tCtx.lineTo(x + 0.5, textureCanvas.height);
          tCtx.stroke();
        }
        for (let y = 0; y <= textureCanvas.height; y += Math.max(1, step)) {
          tCtx.beginPath();
          tCtx.moveTo(0, y + 0.5);
          tCtx.lineTo(textureCanvas.width, y + 0.5);
          tCtx.stroke();
        }
        tCtx.restore();
      }

      const mCtx = miniCanvas.getContext("2d");
      if (mCtx) {
        miniCanvas.width = 220;
        miniCanvas.height = 220;
        drawMiniFront(mCtx, img);
      }
    };

    img.src = objectUrl;

    return () => {
      cancelled = true;
      img.onload = null;
    };
  }, [objectUrl, valid]);

  const hintLegacy = naturalHeight > 0 && naturalHeight <= 32;

  return (
    <div style={{ display: "flex", flexWrap: "wrap", gap: 16, alignItems: "flex-start" }}>
      <div
        style={{
          padding: 12,
          borderRadius: 12,
          background: "rgba(0,0,0,0.22)",
          border: "1px solid rgba(255,255,255,0.08)"
        }}
      >
        <p style={{ margin: "0 0 8px", fontWeight: 600 }}>Texture</p>
        <canvas
          ref={textureRef}
          style={{
            display: "block",
            imageRendering: "pixelated",
            borderRadius: 8,
            border: "1px solid rgba(255,255,255,0.12)"
          }}
        />
      </div>
      <div
        style={{
          padding: 12,
          borderRadius: 12,
          background: "rgba(0,0,0,0.22)",
          border: "1px solid rgba(255,255,255,0.08)"
        }}
      >
        <p style={{ margin: "0 0 8px", fontWeight: 600 }}>Front preview</p>
        <canvas
          ref={miniRef}
          width={220}
          height={220}
          style={{
            display: "block",
            imageRendering: "pixelated",
            borderRadius: 8,
            border: "1px solid rgba(255,255,255,0.12)"
          }}
        />
        <p style={{ margin: "10px 0 0", opacity: 0.82, fontSize: 12, maxWidth: 220 }}>
          Preview approximates in-game layout.
          {hintLegacy ? " 64\u00d732 skins mirror some limbs from the right side." : ""}
        </p>
      </div>
      <p
        style={{
          flex: "1 1 200px",
          margin: 0,
          alignSelf: "center",
          color: valid ? "#7de8b8" : "#ffb3b3",
          fontWeight: 600
        }}
      >
        {label}
      </p>
    </div>
  );
}
