import React, { useEffect, useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { signup } from '../../api/authApi'
import { getFacilities } from '../../api/facilityApi'

export default function Signup() {
    const navigate = useNavigate()

    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')
    const [facilities, setFacilities] = useState([])
    const [selectedFacilityIds, setSelectedFacilityIds] = useState([])
    const [error, setError] = useState('')
    const [loading, setLoading] = useState(false)

    useEffect(() => {
        getFacilities()
            .then((data) => setFacilities(data || []))
            .catch(() => setFacilities([]))
    }, [])

    const toggleFacility = (id) => {
        setSelectedFacilityIds((prev) =>
            prev.includes(id) ? prev.filter((f) => f !== id) : [...prev, id]
        )
    }

    const handleSubmit = async (e) => {
        e.preventDefault()
        setError('')
        setLoading(true)

        try {
            await signup(username, password, selectedFacilityIds)
            navigate('/login')
        } catch (err) {
            setError(
                err.response?.status === 409 || err.response?.status === 400
                    ? 'Username already exists.'
                    : 'Unable to create account. Please try again.'
            )
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

                <h1 className="text-2xl font-semibold text-white mb-2">
                    Create Account
                </h1>

                <p className="text-sm text-slate-400 mb-6">
                    Sign up to get started. Facility access requires admin approval.
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
                            placeholder="Choose a username"
                        />
                    </div>

                    <div>
                        <label className="block text-sm text-slate-400 mb-1">Password</label>
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            minLength={6}
                            className="w-full rounded-xl border border-slate-700 bg-slate-800 px-4 py-2.5 text-sm text-white placeholder:text-slate-500 outline-none focus:border-cyan-400"
                            placeholder="Create a password"
                        />
                    </div>

                    <div>
                        <label className="block text-sm text-slate-400 mb-2">
                            Facilities you need access to
                        </label>
                        <div className="space-y-2 max-h-40 overflow-y-auto rounded-xl border border-slate-700 bg-slate-800 p-3">
                            {facilities.length === 0 && (
                                <p className="text-xs text-slate-500">No facilities available.</p>
                            )}
                            {facilities.map((f) => (
                                <label key={f.id} className="flex items-center gap-2 text-sm text-slate-300">
                                    <input
                                        type="checkbox"
                                        checked={selectedFacilityIds.includes(f.id)}
                                        onChange={() => toggleFacility(f.id)}
                                        className="h-4 w-4 rounded border-slate-600 text-cyan-400 focus:ring-cyan-400"
                                    />
                                    {f.name}
                                </label>
                            ))}
                        </div>
                    </div>

                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full rounded-full bg-cyan-400 py-2.5 text-sm font-semibold text-slate-950 transition hover:bg-cyan-300 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        {loading ? 'Creating account...' : 'Sign up'}
                    </button>
                </form>

                <p className="mt-6 text-center text-sm text-slate-400">
                    Already have an account?{' '}
                    <Link to="/login" className="font-medium text-cyan-400 hover:text-cyan-300">
                        Sign in
                    </Link>
                </p>
            </div>
        </main>
    )
}