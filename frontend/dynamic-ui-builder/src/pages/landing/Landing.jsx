import React from 'react'
import { Link } from 'react-router-dom'

export default function Landing() {
  return (
    <main className="min-h-screen bg-slate-950 text-slate-100">
      <section className="mx-auto flex min-h-screen max-w-5xl items-center px-6 py-16 lg:px-10">
        <div className="max-w-2xl">
          <p className="text-sm font-semibold uppercase tracking-[0.35em] text-cyan-300">
            Dynamic UI Builder
          </p>
          <h1 className="mt-5 text-4xl font-semibold tracking-tight sm:text-5xl">
            Build the app shell, then move into the admin panel.
          </h1>
          <p className="mt-5 text-base leading-7 text-slate-300 sm:text-lg">
            Use the button below to open the admin panel route. The admin page is
            split into a navbar and a sidebar component.
          </p>
          <div className="mt-8">
            <Link
              to="/admin_panel/overview"
              className="inline-flex rounded-full bg-cyan-400 px-6 py-3 text-sm font-semibold text-slate-950 transition hover:bg-cyan-300"
            >
              Go to admin panel
            </Link>
            
          </div>
        </div>
      </section>
    </main>
  )
}