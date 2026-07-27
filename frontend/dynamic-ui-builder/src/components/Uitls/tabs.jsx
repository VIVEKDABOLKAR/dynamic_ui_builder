import React from "react";

export default function Tabs({
  tabs,
  active,
  onChange,
  className = "",
}) {
  return (
    <div
      className={`
        flex
        gap-1
        rounded-xl
        bg-slate-100
        p-1
        ${className}
      `}
    >
      {tabs.map((tab) => {
        const activeTab = active === tab.key;

        return (
          <button
            key={tab.key}
            onClick={() => onChange(tab.key)}
            className={`
              relative
              rounded-lg
              px-5
              py-2.5
              text-sm
              font-medium
              transition-all
              duration-300

              ${
                activeTab
                  ? "bg-white text-blue-600 shadow-sm"
                  : "text-slate-600 hover:bg-white/60"
              }
            `}
          >
            <div className="flex items-center gap-2">
              {tab.icon}
              {tab.label}
            </div>

            {activeTab && (
              <span
                className="
                  absolute
                  bottom-0
                  left-3
                  right-3
                  h-0.5
                  rounded-full
                  bg-blue-600
                "
              />
            )}
          </button>
        );
      })}
    </div>
  );
} 