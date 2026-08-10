import React from "react";
import { useFacility } from "../../context/FacilityV2Context";

export default function DynamicPageDefault() {
    const { selectedFacility } = useFacility();
  return (
    <div
      style={{
        minHeight: "100%",
        padding: "32px",
        background: "#f8fafc",
      }}
    >
      {/* Welcome Section */}
      <div style={{ marginBottom: "28px" }}>
        <h1
          style={{
            margin: 0,
            fontSize: "28px",
            fontWeight: 600,
            color: "#1e293b",
          }}
        >
          Welcome
        </h1>

        <p
          style={{
            marginTop: "8px",
            marginBottom: 0,
            fontSize: "15px",
            color: "#64748b",
          }}
        >
          Select a page from the navigation menu to get started.
        </p>
      </div>

      {/* Selected Facility */}
      <div
        style={{
          background: "#ffffff",
          border: "1px solid #e2e8f0",
          borderRadius: "10px",
          padding: "20px",
          marginBottom: "24px",
        }}
      >
        <div
          style={{
            fontSize: "13px",
            color: "#64748b",
            marginBottom: "6px",
          }}
        >
         Facility
        </div>

        <div
          style={{
            fontSize: "20px",
            fontWeight: 600,
            color: "#0f172a",
          }}
        >
          {selectedFacility?.name || "No facility selected"}
        </div>

        {selectedFacility?.id && (
          <div
            style={{
              marginTop: "6px",
              fontSize: "13px",
              color: "#94a3b8",
            }}
          >
            Facility ID: {selectedFacility.id}
          </div>
        )}
      </div>

      {/* Quick Actions */}
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))",
          gap: "16px",
        }}
      >
        <div
          style={{
            background: "#ffffff",
            border: "1px solid #e2e8f0",
            borderRadius: "10px",
            padding: "20px",
          }}
        >
          <div style={{ fontSize: "20px", marginBottom: "10px" }}>
            📋
          </div>

          <div
            style={{
              fontWeight: 600,
              color: "#1e293b",
              marginBottom: "6px",
            }}
          >
            Pages
          </div>

          <div
            style={{
              fontSize: "14px",
              color: "#64748b",
            }}
          >
            Choose a page from the navigation menu.
          </div>
        </div>

        <div
          style={{
            background: "#ffffff",
            border: "1px solid #e2e8f0",
            borderRadius: "10px",
            padding: "20px",
          }}
        >
          <div style={{ fontSize: "20px", marginBottom: "10px" }}>
            🏢
          </div>

          <div
            style={{
              fontWeight: 600,
              color: "#1e293b",
              marginBottom: "6px",
            }}
          >
            Facility
          </div>

          <div
            style={{
              fontSize: "14px",
              color: "#64748b",
            }}
          >
            Your current facility is shown above.
          </div>
        </div>

      </div>
    </div>
  );
}

