import React from 'react'
import { Link } from 'react-router-dom'

export default function Landing() {
  return (
    <main className="relative min-h-screen overflow-hidden bg-slate-950 text-slate-100">
      {/* subtle grid + glow backdrop */}
      <div
        className="pointer-events-none absolute inset-0 opacity-[0.07]"
        style={{
          backgroundImage:
            'linear-gradient(to right, #ffffff 1px, transparent 1px), linear-gradient(to bottom, #ffffff 1px, transparent 1px)',
          backgroundSize: '56px 56px',
        }}
      />
      <div className="pointer-events-none absolute -top-40 left-1/2 h-[32rem] w-[32rem] -translate-x-1/2 rounded-full bg-cyan-500/20 blur-[120px]" />

      <section className="relative mx-auto flex min-h-screen max-w-5xl items-center px-6 py-16 lg:px-10">
        <div className="max-w-2xl animate-[fadeUp_.5s_ease_both]">
          <div className="flex items-center gap-2">
            <span className="h-1.5 w-1.5 rounded-full bg-cyan-400" />
            <p className="text-sm font-semibold uppercase tracking-[0.35em] text-cyan-300">
              Dynamic UI Builder
            </p>
          </div>

          <h1 className="mt-5 text-4xl font-semibold tracking-tight text-white sm:text-5xl">
            Build the app shell, then move into the admin panel.
          </h1>

          <p className="mt-5 text-base leading-7 text-slate-400 sm:text-lg">
            Use the button below to open the admin panel route. The admin page is
            split into a navbar and a sidebar component.
          </p>

          <div className="mt-8">
            <Link
              to="/admin_panel/overview"
              className="group inline-flex items-center gap-2 rounded-full bg-cyan-400 px-6 py-3 text-sm font-semibold text-slate-950 shadow-[0_0_0_0_rgba(34,211,238,0.4)] transition hover:bg-cyan-300 hover:shadow-[0_0_24px_4px_rgba(34,211,238,0.35)] focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-cyan-300"
            >
              Go to admin panel
              <span className="transition-transform group-hover:translate-x-0.5">→</span>
            </Link>
          </div>
        </div>
      </section>

      <style>{`
        @keyframes fadeUp { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
        @media (prefers-reduced-motion: reduce) {
          .animate-\\[fadeUp_\\.5s_ease_both\\] { animation: none; }
        }
      `}</style>
    </main>
  )
}
