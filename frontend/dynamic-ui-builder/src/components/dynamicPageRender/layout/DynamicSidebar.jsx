import React, { useEffect, useState } from 'react'
import { NavLink } from 'react-router-dom'
import * as FaIcons from "react-icons/fa";
import { FaChevronDown, FaRegCircle } from "react-icons/fa";
import { resolveNavigation } from '../../../api/routeApi'

// icon field from the backend is a free-text string (e.g. "Page1_icon").
// Try to resolve it against a real lucide-react icon name; fall back to a
// plain dot so a bad/placeholder value never breaks the render.
function NavIcon({ name, className }) {
    const pascal = (name || "")
        .replace(/[_-]+/g, " ")
        .replace(/(^\w|\s\w)/g, (m) => m.toUpperCase())
        .replace(/\s+/g, "");

    const Icon = (name && FaIcons[`Fa${pascal}`]) || FaRegCircle;

    return <Icon className={className} />;
}


function NavGroup({ node, depth }) {
    const [open, setOpen] = useState(depth === 0)

    return (
        <div>
            <button
                type="button"
                onClick={() => setOpen((o) => !o)}
                style={{ paddingLeft: `${16 + depth * 16}px` }}
                className="
        group
        flex
        w-full
        items-center
        gap-3
        rounded-xl
        px-4
        py-3
        text-sm
        font-medium
        text-slate-300
        transition-all
        duration-200
        hover:bg-slate-800
        hover:text-white
        hover:shadow-lg
    "
            >
                <NavIcon
                    name={node.icon}
                    className="h-5 w-5 text-slate-400 transition group-hover:scale-110 group-hover:text-cyan-400"
                />

                <span className="flex-1 truncate text-left">
                    {node.label}
                </span>

                <FaChevronDown
                    className={`text-slate-500 transition-transform duration-300 ${open ? "rotate-0" : "-rotate-90"
                        }`}
                />
            </button>


            {open && (
                <div className="mt-0.5 space-y-0.5">
                    {node.children.map((child) => (
                        <NavNode key={child.routeCode ?? child.label} node={child} depth={depth + 1} />
                    ))}
                </div>
            )}
        </div>
    )
}

function NavLeaf({ node, depth }) {
    return (
        <NavLink
            to={"/ui_demo/" + node.path}
            style={{ paddingLeft: `${16 + depth * 16}px` }}
            className={({ isActive }) =>
                `
        group
        relative
        flex
        items-center
        gap-3
        rounded-xl
        px-4
        py-3
        text-sm
        transition-all
        duration-200
        ${isActive
                    ? "bg-cyan-500/15 text-cyan-300 border-l-4 border-cyan-400 shadow-lg"
                    : "text-slate-400 hover:bg-slate-800 hover:text-white"
                }
    `
            }
        >
            <NavIcon
                name={node.icon}
                className="h-5 w-5 transition-all group-hover:scale-110"
            />

            <span className="truncate">
                {node.label}
            </span>
        </NavLink>

    )
}

// A node is a group if it carries children; a leaf if it carries a path.
function NavNode({ node, depth }) {
    const hasChildren = Array.isArray(node.children) && node.children.length > 0
    return hasChildren
        ? <NavGroup node={node} depth={depth} />
        : <NavLeaf node={node} depth={depth} />
}

export default function DynamicSideBar() {
    const [tree, setTree] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)

    useEffect(() => {
        const fetchSidebarDetails = async () => {
            try {
                const response = await resolveNavigation()
                console.log(response);
                
                setTree(response ?? [])
            } catch (err) {
                console.error('failed to load sidebar details', err)
                setError('Could not load navigation.')
            } finally {
                setLoading(false)
            }
        }

        fetchSidebarDetails()
    }, [])

    return (
        <aside className="w-72 h-screen bg-gradient-to-b from-slate-950 via-slate-900 to-slate-950 border-r border-slate-800 shadow-2xl">
            <nav className="flex-1 overflow-y-auto px-3 py-5 space-y-1 scrollbar-thin scrollbar-thumb-slate-700 scrollbar-track-transparent">
                {loading && (
                    <p className="px-3 py-2 text-sm text-slate-500">Loading navigation…</p>
                )}

                {!loading && error && (
                    <p className="px-3 py-2 text-sm text-red-400">{error}</p>
                )}

                {!loading && !error && tree.length === 0 && (
                    <p className="px-3 py-2 text-sm text-slate-500">No navigation items.</p>
                )}

                {!loading && !error && tree.map((node) => (
                    <NavNode key={node.routeCode ?? node.label} node={node} depth={0} />
                ))}
            </nav>
        </aside>
    )
}