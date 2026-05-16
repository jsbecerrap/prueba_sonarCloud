import type { FC } from "react";

interface Props {
  categoria: string;
  sector: string;
  onSelect: (categoria: string, sector: string) => void;
}

const StadiumMap: FC<Props> = ({ categoria, sector, onSelect }) => {
  return (
    <svg viewBox="0 0 480 300" width="100%" style={{ maxWidth: 560 }}>
      <rect x="0" y="0" width="480" height="300" rx="16" fill="#0d1a0d"/>

      {/* BARRA NORTE - izquierda */}
      <rect x="4" y="4" width="66" height="292" rx="12"
        fill={categoria === "BARRA" && sector === "Norte" ? "rgba(100,200,100,0.22)" : "rgba(100,180,100,0.07)"}
        stroke={categoria === "BARRA" && sector === "Norte" ? "#4caf50" : "#2a4a2a"}
        strokeWidth={categoria === "BARRA" && sector === "Norte" ? "2" : "1"}
        style={{ cursor: "pointer" }}
        onClick={() => onSelect("BARRA", "Norte")}
      />
      {[14, 24, 34, 44, 54].map((x) => (
        <rect key={x} x={x} y="12" width="6" height="276" rx="3"
          fill={categoria === "BARRA" && sector === "Norte" ? "rgba(76,175,80,0.35)" : "rgba(76,175,80,0.1)"}
          style={{ pointerEvents: "none" }}
        />
      ))}
      <text x="37" y="152"
        fill={categoria === "BARRA" && sector === "Norte" ? "#66bb6a" : "#2e5c2e"}
        fontSize="9" textAnchor="middle" fontWeight="800" letterSpacing="2"
        transform="rotate(-90,37,152)" style={{ pointerEvents: "none" }}>
        BARRA NORTE
      </text>

      {/* BARRA SUR - derecha */}
      <rect x="410" y="4" width="66" height="292" rx="12"
        fill={categoria === "BARRA" && sector === "Sur" ? "rgba(100,200,100,0.22)" : "rgba(100,180,100,0.07)"}
        stroke={categoria === "BARRA" && sector === "Sur" ? "#4caf50" : "#2a4a2a"}
        strokeWidth={categoria === "BARRA" && sector === "Sur" ? "2" : "1"}
        style={{ cursor: "pointer" }}
        onClick={() => onSelect("BARRA", "Sur")}
      />
      {[416, 426, 436, 446, 456].map((x) => (
        <rect key={x} x={x} y="12" width="6" height="276" rx="3"
          fill={categoria === "BARRA" && sector === "Sur" ? "rgba(76,175,80,0.35)" : "rgba(76,175,80,0.1)"}
          style={{ pointerEvents: "none" }}
        />
      ))}
      <text x="443" y="152"
        fill={categoria === "BARRA" && sector === "Sur" ? "#66bb6a" : "#2e5c2e"}
        fontSize="9" textAnchor="middle" fontWeight="800" letterSpacing="2"
        transform="rotate(90,443,152)" style={{ pointerEvents: "none" }}>
        BARRA SUR
      </text>

      {/* PALCO VIP - arriba izquierda */}
      <rect x="74" y="4" width="158" height="56" rx="8"
        fill={categoria === "PALCO" ? "rgba(180,100,255,0.22)" : "rgba(150,80,220,0.07)"}
        stroke={categoria === "PALCO" ? "#9c27b0" : "#3a1a4a"}
        strokeWidth={categoria === "PALCO" ? "2" : "1"}
        style={{ cursor: "pointer" }}
        onClick={() => onSelect("PALCO", "Occidental VIP")}
      />
      {[10, 18, 26, 34, 42].map((dy) => (
        <rect key={dy} x="82" y={4 + dy} width="142" height="6" rx="3"
          fill={categoria === "PALCO" ? "rgba(156,39,176,0.35)" : "rgba(156,39,176,0.1)"}
          style={{ pointerEvents: "none" }}
        />
      ))}
      <text x="153" y="36"
        fill={categoria === "PALCO" ? "#ce93d8" : "#5a2a6a"}
        fontSize="9" textAnchor="middle" fontWeight="800" letterSpacing="2"
        style={{ pointerEvents: "none" }}>
        PALCO VIP
      </text>

      {/* GENERAL OCC - arriba derecha */}
      <rect x="238" y="4" width="166" height="56" rx="8"
        fill={categoria === "GENERAL" && sector === "Occidental" ? "rgba(255,180,0,0.22)" : "rgba(220,150,0,0.07)"}
        stroke={categoria === "GENERAL" && sector === "Occidental" ? "#ffa000" : "#3a2a00"}
        strokeWidth={categoria === "GENERAL" && sector === "Occidental" ? "2" : "1"}
        style={{ cursor: "pointer" }}
        onClick={() => onSelect("GENERAL", "Occidental")}
      />
      {[10, 18, 26, 34, 42].map((dy) => (
        <rect key={dy} x="246" y={4 + dy} width="150" height="6" rx="3"
          fill={categoria === "GENERAL" && sector === "Occidental" ? "rgba(255,160,0,0.35)" : "rgba(255,160,0,0.1)"}
          style={{ pointerEvents: "none" }}
        />
      ))}
      <text x="321" y="36"
        fill={categoria === "GENERAL" && sector === "Occidental" ? "#ffcc02" : "#6a5000"}
        fontSize="9" textAnchor="middle" fontWeight="800" letterSpacing="2"
        style={{ pointerEvents: "none" }}>
        GENERAL OCC
      </text>

      {/* GENERAL ORIENTAL - abajo */}
      <rect x="74" y="240" width="330" height="56" rx="8"
        fill={categoria === "GENERAL" && sector === "Oriental" ? "rgba(255,180,0,0.22)" : "rgba(220,150,0,0.07)"}
        stroke={categoria === "GENERAL" && sector === "Oriental" ? "#ffa000" : "#3a2a00"}
        strokeWidth={categoria === "GENERAL" && sector === "Oriental" ? "2" : "1"}
        style={{ cursor: "pointer" }}
        onClick={() => onSelect("GENERAL", "Oriental")}
      />
      {[10, 18, 26, 34, 42].map((dy) => (
        <rect key={dy} x="82" y={240 + dy} width="314" height="6" rx="3"
          fill={categoria === "GENERAL" && sector === "Oriental" ? "rgba(255,160,0,0.35)" : "rgba(255,160,0,0.1)"}
          style={{ pointerEvents: "none" }}
        />
      ))}
      <text x="239" y="272"
        fill={categoria === "GENERAL" && sector === "Oriental" ? "#ffcc02" : "#6a5000"}
        fontSize="9" textAnchor="middle" fontWeight="800" letterSpacing="2"
        style={{ pointerEvents: "none" }}>
        GENERAL ORIENTAL
      </text>

      {/* CANCHA */}
      <rect x="74" y="64" width="332" height="172" rx="5" fill="#1b5e20" stroke="#2e7d32" strokeWidth="2" style={{ pointerEvents: "none" }}/>
      <rect x="74" y="64" width="332" height="172" rx="5" fill="none" stroke="#388e3c" strokeWidth="1" style={{ pointerEvents: "none" }}/>
      <line x1="240" y1="64" x2="240" y2="236" stroke="#388e3c" strokeWidth="1" style={{ pointerEvents: "none" }}/>
      <circle cx="240" cy="150" r="26" fill="none" stroke="#388e3c" strokeWidth="1" style={{ pointerEvents: "none" }}/>
      <circle cx="240" cy="150" r="2.5" fill="#388e3c" style={{ pointerEvents: "none" }}/>
      <rect x="74" y="112" width="50" height="76" fill="none" stroke="#388e3c" strokeWidth="1" style={{ pointerEvents: "none" }}/>
      <rect x="74" y="128" width="22" height="44" fill="none" stroke="#388e3c" strokeWidth="0.8" style={{ pointerEvents: "none" }}/>
      <path d="M124,118 A30,30 0 0,1 124,182" fill="none" stroke="#388e3c" strokeWidth="0.8" style={{ pointerEvents: "none" }}/>
      <rect x="356" y="112" width="50" height="76" fill="none" stroke="#388e3c" strokeWidth="1" style={{ pointerEvents: "none" }}/>
      <rect x="384" y="128" width="22" height="44" fill="none" stroke="#388e3c" strokeWidth="0.8" style={{ pointerEvents: "none" }}/>
      <path d="M356,118 A30,30 0 0,0 356,182" fill="none" stroke="#388e3c" strokeWidth="0.8" style={{ pointerEvents: "none" }}/>
      <path d="M74,64 A7,7 0 0,1 81,71" fill="none" stroke="#388e3c" strokeWidth="0.8" style={{ pointerEvents: "none" }}/>
      <path d="M406,64 A7,7 0 0,0 399,71" fill="none" stroke="#388e3c" strokeWidth="0.8" style={{ pointerEvents: "none" }}/>
      <path d="M74,236 A7,7 0 0,0 81,229" fill="none" stroke="#388e3c" strokeWidth="0.8" style={{ pointerEvents: "none" }}/>
      <path d="M406,236 A7,7 0 0,1 399,229" fill="none" stroke="#388e3c" strokeWidth="0.8" style={{ pointerEvents: "none" }}/>
      <circle cx="108" cy="150" r="2" fill="#388e3c" style={{ pointerEvents: "none" }}/>
      <circle cx="372" cy="150" r="2" fill="#388e3c" style={{ pointerEvents: "none" }}/>
    </svg>
  );
};

export default StadiumMap;