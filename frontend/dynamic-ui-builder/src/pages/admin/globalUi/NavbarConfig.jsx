import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import toast from 'react-hot-toast'
import {
  getNavbarStyle,
  saveNavbarStyle,
  getNavbarComponents,
  saveNavbarComponents,
} from '../../../api/globalUiApi'
import { getFacilities } from '../../../api/facilityApi'

const EMPTY_STYLE = {
  backgroundColor: '',
  textColor: '',
  height: '',
  borderStyle: '',
}

const EMPTY_COMPONENTS = {
  showLogo: true,
  logoUrl: '',
  showFacilities: true,
  showProfile: true,
}

function Toggle({ checked, onChange, label }) {
  return (
    <label className="flex items-center gap-2 text-sm text-slate-700">
      <input
        type="checkbox"
        checked={checked}
        onChange={(e) => onChange(e.target.checked)}
        className="h-4 w-4 rounded border-slate-300 text-blue-700 focus:ring-blue-600"
      />
      {label}
    </label>
  )
}

function SectionCard({ title, children, collapsible = true }) {
  const [open, setOpen] = useState(true)
  return (
    <div className="rounded-xl border border-slate-200 bg-white shadow-sm">
      <button
        type="button"
        onClick={() => collapsible && setOpen((o) => !o)}
        className="flex w-full items-center justify-between px-5 py-3 text-left"
      >
        <span className="text-sm font-semibold text-slate-900">{title}</span>
        {collapsible && <span className="text-slate-400">{open ? '︿' : '﹀'}</span>}
      </button>
      {open && <div className="border-t border-slate-100 px-5 py-4">{children}</div>}
    </div>
  )
}

export default function NavbarConfig() {
  // ---- Styling (global, no facility) ----
  const [style, setStyle] = useState(EMPTY_STYLE)
  const [styleLoading, setStyleLoading] = useState(true)
  const [styleSaving, setStyleSaving] = useState(false)

  // ---- Components (per facility) ----
  const [facilities, setFacilities] = useState([])
  const [facilitiesLoading, setFacilitiesLoading] = useState(true)
  const [selectedFacilityId, setSelectedFacilityId] = useState(null)
  const [components, setComponents] = useState(EMPTY_COMPONENTS)
  const [componentsLoading, setComponentsLoading] = useState(false)
  const [componentsSaving, setComponentsSaving] = useState(false)

  const selectedFacility = facilities.find((f) => f.id === selectedFacilityId) || null

  // Load global style once
  useEffect(() => {
    let mounted = true
    getNavbarStyle()
      .then((data) => mounted && setStyle({ ...EMPTY_STYLE, ...(data || {}) }))
      .catch(() => mounted && toast.error('Failed to load navbar styling'))
      .finally(() => mounted && setStyleLoading(false))
    return () => { mounted = false }
  }, [])

  // Load facilities once
  useEffect(() => {
    let mounted = true
    getFacilities()
      .then((data) => {
        if (!mounted) return
        setFacilities(data || [])
        if (data?.length) setSelectedFacilityId(data[0].id)
      })
      .catch(() => mounted && toast.error('Failed to load facilities'))
      .finally(() => mounted && setFacilitiesLoading(false))
    return () => { mounted = false }
  }, [])

  // Load components whenever selected facility changes
  useEffect(() => {
    if (!selectedFacilityId) return
    let mounted = true
    setComponentsLoading(true)
    getNavbarComponents(selectedFacilityId)
      .then((data) => mounted && setComponents({ ...EMPTY_COMPONENTS, ...(data || {}) }))
      .catch(() => mounted && toast.error('Failed to load navbar components'))
      .finally(() => mounted && setComponentsLoading(false))
    return () => { mounted = false }
  }, [selectedFacilityId])

  const updateStyle = (key, value) => setStyle((prev) => ({ ...prev, [key]: value }))
  const updateComponents = (key, value) => setComponents((prev) => ({ ...prev, [key]: value }))

  const handleSaveStyle = async () => {
    setStyleSaving(true)
    try {
      await saveNavbarStyle(style)
      toast.success('Styling saved — applies to all facilities')
    } catch (e) {
      console.log(e)
      toast.error('Failed to save styling')
    } finally {
      setStyleSaving(false)
    }
  }

  const handleSaveComponents = async () => {
    if (!selectedFacility) return
    setComponentsSaving(true)
    try {
      await saveNavbarComponents(selectedFacility.id, components)
      toast.success(`Component settings saved for ${selectedFacility.name}`)
    } catch (e) {
      toast.error('Failed to save component settings')
    } finally {
      setComponentsSaving(false)
    }
  }

  if (facilitiesLoading || styleLoading) {
    return <div className="p-6 text-sm text-slate-500">Loading navbar configuration…</div>
  }

  return (
    <div className="flex flex-col gap-6">
      <div>
        <div className="flex items-center gap-1 text-xs text-slate-400">
          <Link to="/admin_panel/global-ui" className="hover:text-slate-600">Global UI</Link>
          <span>›</span><span>Navbar</span><span>›</span>
          <span className="text-slate-500">Configure</span>
        </div>
        <h1 className="mt-2 text-2xl font-bold text-slate-900">Global Navbar Configuration</h1>
        <p className="mt-1 text-sm text-slate-500">
          Styling applies to every facility. Component visibility (logo, facility switcher, profile) can be set per facility.
        </p>
      </div>

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-[1fr_420px]">
        <div className="flex flex-col gap-4">
          {/* ---- STYLING (global) ---- */}
          <SectionCard title="Styling (applies to all facilities)">
            <div className="grid grid-cols-2 gap-4 sm:grid-cols-4">
              <div>
                <label className="mb-1 block text-xs font-medium text-slate-500">Background Color</label>
                <div className="flex items-center gap-2 rounded-lg border border-slate-200 px-2 py-1.5">
                  <input
                    type="color"
                    value={style.backgroundColor || '#1E3A8A'}
                    onChange={(e) => updateStyle('backgroundColor', e.target.value)}
                    className="h-6 w-6 cursor-pointer rounded border-0 bg-transparent p-0"
                  />
                  <input
                    value={style.backgroundColor}
                    onChange={(e) => updateStyle('backgroundColor', e.target.value)}
                    placeholder="#1E3A8A"
                    className="w-full text-sm outline-none"
                  />
                </div>
              </div>
              <div>
                <label className="mb-1 block text-xs font-medium text-slate-500">Text Color</label>
                <div className="flex items-center gap-2 rounded-lg border border-slate-200 px-2 py-1.5">
                  <input
                    type="color"
                    value={style.textColor || '#FFFFFF'}
                    onChange={(e) => updateStyle('textColor', e.target.value)}
                    className="h-6 w-6 cursor-pointer rounded border-0 bg-transparent p-0"
                  />
                  <input
                    value={style.textColor}
                    onChange={(e) => updateStyle('textColor', e.target.value)}
                    placeholder="#FFFFFF"
                    className="w-full text-sm outline-none"
                  />
                </div>
              </div>
              <div>
                <label className="mb-1 block text-xs font-medium text-slate-500">Height</label>
                <input
                  value={style.height}
                  onChange={(e) => updateStyle('height', e.target.value)}
                  placeholder="e.g. 64px"
                  className="w-full rounded-lg border border-slate-200 px-2 py-2 text-sm"
                />
              </div>
              <div>
                <label className="mb-1 block text-xs font-medium text-slate-500">Border Style</label>
                <select
                  value={style.borderStyle}
                  onChange={(e) => updateStyle('borderStyle', e.target.value)}
                  className="w-full rounded-lg border border-slate-200 px-2 py-2 text-sm"
                >
                  <option value="None">None</option>
                  <option value="Solid">Solid</option>
                  <option value="Shadow">Shadow</option>
                </select>
              </div>
            </div>

            <div className="mt-4 flex justify-end">
              <button
                type="button"
                onClick={handleSaveStyle}
                disabled={styleSaving}
                className="flex items-center gap-2 rounded-lg bg-blue-800 px-4 py-2 text-sm font-medium text-white hover:bg-blue-900 disabled:opacity-60"
              >
                💾 {styleSaving ? 'Saving…' : 'Save Style'}
              </button>
            </div>
          </SectionCard>

          {/* ---- COMPONENTS (per facility) ---- */}
          <SectionCard title="Components (per facility)">
            <div className="mb-4">
              <label className="mb-1 block text-xs font-medium text-slate-500">Facility</label>
              <select
                value={selectedFacilityId || ''}
                onChange={(e) => setSelectedFacilityId(e.target.value)}
                className="w-full rounded-lg border border-slate-200 px-3 py-2 text-sm"
              >
                {facilities.map((f) => (
                  <option key={f.id} value={f.id}>{f.name}</option>
                ))}
              </select>
            </div>

            {componentsLoading ? (
              <p className="text-sm text-slate-400">Loading component settings…</p>
            ) : (
              <>
                <div>
                  <label className="mb-1 block text-xs font-medium text-slate-500">Logo URL</label>
                  <input
                    value={components.logoUrl}
                    onChange={(e) => updateComponents('logoUrl', e.target.value)}
                    placeholder="https://.../logo.png"
                    className="w-full rounded-lg border border-slate-200 px-3 py-2 text-sm"
                  />
                </div>

                <div className="mt-4 flex flex-wrap gap-x-6 gap-y-3">
                  <Toggle checked={components.showLogo} onChange={(v) => updateComponents('showLogo', v)} label="Show Logo" />
                  <Toggle checked={components.showFacilities} onChange={(v) => updateComponents('showFacilities', v)} label="Show Facility Switcher" />
                  <Toggle checked={components.showProfile} onChange={(v) => updateComponents('showProfile', v)} label="Show Profile" />
                </div>

                <div className="mt-4 flex justify-end">
                  <button
                    type="button"
                    onClick={handleSaveComponents}
                    disabled={componentsSaving}
                    className="flex items-center gap-2 rounded-lg bg-blue-800 px-4 py-2 text-sm font-medium text-white hover:bg-blue-900 disabled:opacity-60"
                  >
                    💾 {componentsSaving ? 'Saving…' : `Save for ${selectedFacility?.name || ''}`}
                  </button>
                </div>
              </>
            )}
          </SectionCard>
        </div>
            
        {/* ---- LIVE PREVIEW (combines both) ---- */}
        <div
          className="mb-4 flex h-fit items-center justify-between rounded-lg px-4"
          style={{
            backgroundColor: style.backgroundColor || '#1E3A8A',
            color: style.textColor || '#FFFFFF',
            height: style.height || '64px',
          }}
        >
          <div className="flex items-center gap-2">
            {components.showLogo && (
              components.logoUrl ? (
                <img src={components.logoUrl} alt={selectedFacility?.name} className="h-8 w-8 rounded-md object-contain" />
              ) : (
                <span className="flex h-8 w-8 items-center justify-center rounded-md bg-white/15 text-xs font-bold tracking-wide">
                  YMS
                </span>
              )
            )}
          </div>

          <div className="flex items-center gap-3 text-sm">
            {components.showFacilities && (
              <select
                value={selectedFacilityId || ''}
                onChange={(e) => setSelectedFacilityId(e.target.value)}
                className="rounded-md bg-white/15 px-2 py-1 text-sm font-medium outline-none"
                style={{ color: style.textColor || '#FFFFFF' }}
              >
                {facilities.map((f) => (
                  <option key={f.id} value={f.id} className="text-slate-900">{f.name}</option>
                ))}
              </select>
            )}
            {components.showProfile && (
              <span className="flex h-7 w-7 items-center justify-center rounded-full bg-white/20 text-xs font-semibold">
                AD
              </span>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}