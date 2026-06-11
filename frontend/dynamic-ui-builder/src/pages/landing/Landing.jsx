import React from "react";
import { Link } from "react-router-dom";
import { motion } from "framer-motion";

export default function Landing() {
  return (
    <main className="relative min-h-screen overflow-hidden bg-[#020617] text-white">
      {/* Animated Background */}
      <div className="absolute inset-0">
        <div className="absolute left-[-10%] top-[-10%] h-[500px] w-[500px] rounded-full bg-cyan-500/20 blur-[140px] animate-pulse" />
        <div className="absolute bottom-[-10%] right-[-10%] h-[500px] w-[500px] rounded-full bg-purple-500/20 blur-[140px] animate-pulse" />
        <div className="absolute left-1/2 top-1/2 h-[300px] w-[300px] -translate-x-1/2 -translate-y-1/2 rounded-full bg-blue-500/10 blur-[120px]" />
      </div>

      {/* Grid Overlay */}
      <div className="absolute inset-0 bg-[linear-gradient(rgba(255,255,255,0.04)_1px,transparent_1px),linear-gradient(90deg,rgba(255,255,255,0.04)_1px,transparent_1px)] bg-[size:60px_60px]" />

      <section className="relative z-10 mx-auto flex min-h-screen max-w-7xl items-center justify-center px-6 py-16">
        <motion.div
          initial={{ opacity: 0, y: 60 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.9 }}
          className="max-w-3xl text-center"
        >
          {/* Badge */}
          <motion.div
            initial={{ opacity: 0, scale: 0.8 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ delay: 0.2 }}
            className="mx-auto inline-flex items-center rounded-full border border-cyan-400/20 bg-cyan-500/10 px-5 py-2 backdrop-blur-xl"
          >
            <span className="mr-2 h-2 w-2 animate-pulse rounded-full bg-cyan-400" />
            <span className="text-sm font-medium tracking-wider text-cyan-300">
              Dynamic UI Builder
            </span>
          </motion.div>

          {/* Heading */}
          <motion.h1
            initial={{ opacity: 0, y: 25 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.3 }}
            className="mt-8 text-5xl font-black leading-tight sm:text-7xl"
          >
            Create Beautiful
            <span className="block bg-gradient-to-r from-cyan-400 via-blue-500 to-purple-500 bg-clip-text text-transparent">
              Admin Experiences
            </span>
          </motion.h1>

          {/* Description */}
          <motion.p
            initial={{ opacity: 0, y: 25 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.5 }}
            className="mx-auto mt-8 max-w-2xl text-lg leading-8 text-slate-300"
          >
            Design modern dashboards, manage dynamic layouts, and build
            scalable interfaces with an elegant admin experience powered by
            reusable components.
          </motion.p>

          {/* Buttons */}
          <motion.div
            initial={{ opacity: 0, y: 25 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.7 }}
            className="mt-12 flex flex-col justify-center gap-4 sm:flex-row"
          >
            <Link
              to="/admin_panel"
              className="group relative overflow-hidden rounded-2xl bg-gradient-to-r from-cyan-400 to-blue-500 px-8 py-4 font-semibold text-slate-950 shadow-[0_0_40px_rgba(34,211,238,0.4)] transition-all duration-300 hover:scale-105 hover:shadow-[0_0_60px_rgba(34,211,238,0.7)]"
            >
              <span className="relative z-10">
                Open Admin Panel →
              </span>

              <div className="absolute inset-0 -translate-x-full bg-white/30 transition-transform duration-700 group-hover:translate-x-full" />
            </Link>

            <button className="rounded-2xl border border-white/10 bg-white/5 px-8 py-4 font-semibold backdrop-blur-xl transition-all duration-300 hover:border-cyan-400/40 hover:bg-white/10">
              View Components
            </button>
          </motion.div>

          {/* Floating Cards */}
          <div className="relative mt-20 hidden md:block">
            <motion.div
              animate={{ y: [-10, 10, -10] }}
              transition={{ repeat: Infinity, duration: 6 }}
              className="absolute -left-20 top-0 rounded-3xl border border-white/10 bg-white/5 p-5 backdrop-blur-xl"
            >
              <div className="text-sm text-cyan-300">Dashboard</div>
              <div className="mt-2 text-2xl font-bold">98%</div>
            </motion.div>

            <motion.div
              animate={{ y: [10, -10, 10] }}
              transition={{ repeat: Infinity, duration: 5 }}
              className="absolute -right-20 top-10 rounded-3xl border border-white/10 bg-white/5 p-5 backdrop-blur-xl"
            >
              <div className="text-sm text-purple-300">Users</div>
              <div className="mt-2 text-2xl font-bold">24.5K</div>
            </motion.div>
          </div>
        </motion.div>
      </section>
    </main>
  );
}