import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { login } from '../../api/authApi'


export default function Login() {
  const navigate = useNavigate()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const data = await login(username, password)
      if (data.role === 'ROLE_ADMIN') {
        navigate('/admin_panel/overview', { replace: true })
      } else {
        navigate('/ui_demo/home', { replace: true })
      }
    } catch (err) {
      if (err.response?.status === 429) {
        setError(err.response?.data?.error || 'Too many attempts. Please try again later.')
      } else if (err.response?.status === 401) {
        setError('Invalid username or password.')
      } else {
        setError('Something went wrong. Try again.')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="min-h-screen bg-slate-950 flex items-center justify-center px-4">
      <div className="w-full max-w-sm bg-slate-900 rounded-2xl p-8 border border-slate-800">
        <p className="text-xs font-semibold uppercase tracking-widest text-cyan-300 mb-2">
          Dynamic UI Builder
        </p>
        <h1 className="text-2xl font-semibold text-white mb-2">Sign in</h1>

        <p className="text-sm text-slate-400 mb-6">
          Welcome back. Sign in to continue.
        </p>

        {error && (
          <div className="mb-4 rounded-lg bg-red-500/10 border border-red-500/30 px-4 py-3 text-sm text-red-400">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm text-slate-400 mb-1">Username</label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              className="w-full rounded-xl border border-slate-700 bg-slate-800 px-4 py-2.5 text-sm text-white placeholder:text-slate-500 outline-none focus:border-cyan-400"
              placeholder="Enter username"
            />
          </div>

          <div>
            <label className="block text-sm text-slate-400 mb-1">Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              className="w-full rounded-xl border border-slate-700 bg-slate-800 px-4 py-2.5 text-sm text-white placeholder:text-slate-500 outline-none focus:border-cyan-400"
              placeholder="Enter password"
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full rounded-full bg-cyan-400 py-2.5 text-sm font-semibold text-slate-950 transition hover:bg-cyan-300 disabled:opacity-50 disabled:cursor-not-allowed mt-2"
          >
            {loading ? 'Signing in...' : 'Sign in'}
          </button>
        </form>

        <p className="mt-6 text-center text-sm text-slate-400">
          Don't have an account?{' '}
          <Link
            to="/signup"
            className="font-medium text-cyan-400 hover:text-cyan-300"
          >
            Sign up
          </Link>
        </p>

      </div>
    </main>
  )
}