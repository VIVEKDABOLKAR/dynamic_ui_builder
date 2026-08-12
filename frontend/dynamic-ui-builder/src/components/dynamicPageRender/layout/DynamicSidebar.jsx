import React, { useEffect, useState } from "react";
import { NavLink } from "react-router-dom";
import * as FaIcons from "react-icons/fa";
import {
    FaChevronDown,
    FaRegCircle,
} from "react-icons/fa";
import { resolveNavigation } from "../../../api/routeApi";
import { useFacility } from "../../../context/FacilityV2Context";

// ---------------------------------------------------------
// Icon resolver
// ---------------------------------------------------------
function NavIcon({ name, className }) {
    const pascal = (name || "")
        .replace(/[_-]+/g, " ")
        .replace(/(^\w|\s\w)/g, (m) => m.toUpperCase())
        .replace(/\s+/g, "");

    const Icon =
        (name && FaIcons[`Fa${pascal}`]) || FaIcons.FaBlackberry;

    return <Icon className={className} />;
}

// ---------------------------------------------------------
// Parent menu
// ---------------------------------------------------------
function NavGroup({ node, depth }) {
    // Every parent starts CLOSED
    const [open, setOpen] = useState(false);

    return (
        <div className="mb-1">
            {/* Parent button */}
            <button
                type="button"
                onClick={() => setOpen((value) => !value)}
                style={{
                    paddingLeft: `${12 + depth * 14}px`,
                }}
                className="
          group
          flex
          w-full
          items-center
          gap-3
          rounded-lg
          px-3
          py-2.5
          text-left
          text-sm
          font-medium
          text-slate-600
          transition-all
          duration-200

          hover:bg-slate-100
          hover:text-slate-900

          focus:outline-none
          focus:ring-2
          focus:ring-indigo-500/20
        "
            >
                {/* Icon */}
                <span
                    className="
            flex
            h-8
            w-8
            shrink-0
            items-center
            justify-center
            rounded-lg
            bg-slate-100
            text-slate-500
            transition-all
            duration-200

            group-hover:bg-indigo-50
            group-hover:text-indigo-600
          "
                >
                    <NavIcon
                        name={node.icon}
                        className="
              h-4
              w-4
              transition-transform
              duration-200
              group-hover:scale-110
            "
                    />
                </span>

                {/* Label */}
                <span className="flex-1 truncate text-left">
                    {node.label}
                </span>

                {/* Arrow */}
                <span
                    className="
            flex
            h-6
            w-6
            shrink-0
            items-center
            justify-center
            rounded-md
            text-slate-400
            transition-colors

            group-hover:bg-slate-200
            group-hover:text-slate-600
          "
                >
                    <FaChevronDown
                        className={`
              h-3
              w-3
              transition-transform
              duration-200
              ${open ? "rotate-0" : "-rotate-90"}
            `}
                    />
                </span>
            </button>

            {/* Children */}
            <div
                className={`
          grid
          transition-all
          duration-200
          ease-in-out

          ${open
                        ? "grid-rows-[1fr] opacity-100"
                        : "grid-rows-[0fr] opacity-0"
                    }
        `}
            >
                <div className="overflow-hidden">
                    <div className="ml-5 mt-1 space-y-0.5 border-l border-slate-200 pl-2">
                        {node.children.map((child) => (
                            <NavNode
                                key={child.routeCode ?? child.label}
                                node={child}
                                depth={depth + 1}
                            />
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
}

// ---------------------------------------------------------
// Leaf menu
// ---------------------------------------------------------
function NavLeaf({ node, depth }) {
    return (
        <NavLink
            to={"/ui_demo/" + node.path}
            style={{
                paddingLeft: `${10 + Math.max(depth - 1, 0) * 6}px`,
            }}
            className={({ isActive }) =>
                `
        group
        relative
        flex
        items-center
        gap-3
        rounded-lg
        px-3
        py-2.5
        text-sm
        font-medium
        transition-all
        duration-200

        ${isActive
                    ? `
              bg-indigo-50
              text-indigo-700
              shadow-sm
            `
                    : `
              text-slate-500
              hover:bg-slate-50
              hover:text-slate-900
            `
                }
        `
            }
        >
            {({ isActive }) => (
                <>
                    {/* Active indicator */}
                    {isActive && (
                        <span
                            className="
                absolute
                left-0
                top-1/2
                h-6
                w-1
                -translate-y-1/2
                rounded-r-full
                bg-indigo-600
              "
                        />
                    )}

                    {/* Icon */}
                    <span
                        className={`
              flex
              h-7
              w-7
              shrink-0
              items-center
              justify-center
              rounded-md
              transition-all
              duration-200

              ${isActive
                                ? "bg-white text-indigo-600 shadow-sm"
                                : `
                    bg-transparent
                    text-slate-400
                    group-hover:bg-slate-100
                    group-hover:text-indigo-600
                  `
                            }
            `}
                    >
                        <NavIcon
                            name={node.icon}
                            className="
                h-3.5
                w-3.5
                transition-transform
                duration-200
                group-hover:scale-110
              "
                        />
                    </span>

                    {/* Label */}
                    <span className="truncate">
                        {node.label}
                    </span>

                    {/* Active dot */}
                    {isActive && (
                        <span
                            className="
                ml-auto
                h-1.5
                w-1.5
                shrink-0
                rounded-full
                bg-indigo-600
              "
                        />
                    )}
                </>
            )}
        </NavLink>
    );
}

// ---------------------------------------------------------
// Node
// ---------------------------------------------------------
function NavNode({ node, depth }) {
    const hasChildren =
        Array.isArray(node.children) &&
        node.children.length > 0;

    return hasChildren ? (
        <NavGroup node={node} depth={depth} />
    ) : (
        <NavLeaf node={node} depth={depth} />
    );
}

// ---------------------------------------------------------
// Sidebar
// ---------------------------------------------------------
export default function DynamicSideBar() {
    const [tree, setTree] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const {
        selectedFacility,
        loading: facilityLoading,
    } = useFacility();

    useEffect(() => {
        if (facilityLoading) return;

        const fetchSidebarDetails = async () => {
            setLoading(true);
            setError(null);

            try {
                const response = await resolveNavigation();

                console.log(response);

                setTree(response ?? []);
            } catch (err) {
                console.error(
                    "failed to load sidebar details",
                    err
                );

                setError("Could not load navigation.");
            } finally {
                setLoading(false);
            }
        };

        fetchSidebarDetails();
    }, [facilityLoading, selectedFacility]);

    return (
        <aside
            className="
        flex
        h-dvh
        min-h-0
        w-72
        shrink-0
        flex-col
        overflow-hidden

        border-r
        border-slate-200

        bg-white

        shadow-[2px_0_12px_rgba(15,23,42,0.04)]
      "
        >
            {/* Navigation */}
            <nav
                className="
          flex-1
          min-h-0
          overflow-y-auto
          px-3
          py-4

          scrollbar-thin
          scrollbar-track-transparent
          scrollbar-thumb-slate-200
          hover:scrollbar-thumb-slate-300
        "
            >
                {/* Loading */}
                {loading && (
                    <div className="space-y-2">
                        {[1, 2, 3, 4, 5].map((item) => (
                            <div
                                key={item}
                                className="
                  h-11
                  animate-pulse
                  rounded-lg
                  bg-slate-100
                "
                            />
                        ))}
                    </div>
                )}

                {/* Error */}
                {!loading && error && (
                    <div
                        className="
              rounded-lg
              border
              border-red-100
              bg-red-50
              px-4
              py-3
            "
                    >
                        <p className="text-xs font-medium text-red-600">
                            {error}
                        </p>
                    </div>
                )}

                {/* Empty */}
                {!loading &&
                    !error &&
                    tree.length === 0 && (
                        <div className="px-4 py-10 text-center">
                            <div
                                className="
                  mx-auto
                  mb-3
                  flex
                  h-10
                  w-10
                  items-center
                  justify-center
                  rounded-full
                  bg-slate-100
                  text-slate-400
                "
                            >
                                <FaRegCircle className="h-4 w-4" />
                            </div>

                            <p className="text-xs text-slate-500">
                                No navigation items
                            </p>
                        </div>
                    )}

                {/* Navigation Tree */}
                {!loading &&
                    !error &&
                    tree.length > 0 && (
                        <div className="space-y-1">
                            {tree.map((node) => (
                                <NavNode
                                    key={
                                        node.routeCode ??
                                        node.label
                                    }
                                    node={node}
                                    depth={0}
                                />
                            ))}
                        </div>
                    )}
            </nav>
        </aside>
    );
}