import React, { useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import Tabs from '../../../../components/Uitls/tabs'
import { PAGE_TABS } from './AddPageComponent/tabRegistry'
import usePageForm from './AddPageComponent/usePageForm'

export default function AddNewPage() {
  const navigate = useNavigate()
  const { pageCode } = useParams()
  const [activeTab, setActiveTab] = useState('general')

  const pageForm = usePageForm(pageCode)
  const { isEdit, saving, message, errors, save, validate } = pageForm

  const activeTabConfig = PAGE_TABS.find(tab => tab.key === activeTab)
  const ActiveTab = activeTabConfig.component

  const hasErrors = errors && Object.keys(errors).length > 0

  // Re-run validation whenever the user switches tabs. This doesn't block
  // the switch (data always stays in the shared pageForm state), it just
  // surfaces any validation errors immediately instead of only on submit.
  const handleTabChange = (nextTab) => {
    validate()
    setActiveTab(nextTab)
  }

  const handleSubmit = async () => {
    const success = await save()
    if (success) {
      navigate('/admin_panel/manage_page')
    }
  }

  return (
    <div className="max-w">
      {/* Header */}
      <div className="mb-8 flex items-start justify-between">

        <div>

          <button
            onClick={() => navigate("/admin_panel/manage_page")}
            className="mb-3 flex items-center gap-2 text-sm text-slate-500 hover:text-slate-900"
          >
            ← Back to Pages
          </button>

          <h1 className="text-3xl font-bold tracking-tight text-slate-900">
            {isEdit ? "Edit Page" : "Create New Page"}
          </h1>

          <p className="mt-2 text-sm text-slate-500 max-w-2xl">
            Configure page metadata, navigation, routing and security settings.
          </p>

        </div>

        <div>

          <span
            className="
                rounded-full
                bg-emerald-100
                px-4
                py-2
                text-sm
                font-medium
                text-emerald-700
            "
          >
            {pageForm.formData.status}
          </span>

        </div>

      </div>

      {/* Progress bar */}
      <div className="flex items-center justify-between border-b px-8 py-4">

        <div className="text-sm text-slate-500">
          Step {PAGE_TABS.findIndex(t => t.key === activeTab) + 1}
          {" "}of{" "}
          {PAGE_TABS.length}
        </div>

        <div className="w-72 h-3 bg-slate-200 rounded-full overflow-hidden">
          <div
            className="h-full rounded-full bg-gradient-to-r from-sky-500 to-indigo-600 transition-all duration-500 ease-in-out shadow-[0_0_12px_rgba(59,130,246,0.5)]"
            style={{
              width: `${((PAGE_TABS.findIndex((t) => t.key === activeTab) + 1) /
                PAGE_TABS.length) *
                100
                }%`,
            }}
          />
        </div>



      </div>


      {/* Main Card */}
      <div
        className="
        overflow-hidden
        rounded-2xl
        border
        border-slate-200
        bg-white
        shadow-sm
    "
      >
        {/* Tabs */}
        <div className="border-b bg-slate-50 px-6 py-4">

          <Tabs
            tabs={PAGE_TABS}
            active={activeTab}
            onChange={handleTabChange}
          />

        </div>

        {/* Message / Error banner - this was being computed but never rendered before */}
        {(message || hasErrors) && (
          <div className="px-6 pt-4">

            <div
              className="
            flex
            items-start
            gap-3
            rounded-xl
            border
            border-red-200
            bg-red-50
            px-4
            py-4
        "
            >

              <div className="text-xl">
                ⚠️
              </div>

              <div>

                <div className="font-semibold text-red-700">
                  Validation Error
                </div>

                <div className="mt-1 text-sm text-red-600">
                  Please complete all required fields before saving.
                </div>

              </div>

            </div>

          </div>
        )}

        {/* Content */}
        <div className="min-h-[520px] p-8">
          <ActiveTab pageForm={pageForm} />
        </div>

        {/* Footer */}
        <div
          className="
        flex
        items-center
        justify-between
        border-t
        bg-slate-50
        px-8
        py-5
    "
        >

          <div className="text-sm text-slate-500">

            {isEdit
              ? "Editing existing page"
              : "New page configuration"}

          </div>

          <div className="flex gap-3">

            <button
              type="button"
              onClick={() => navigate("/admin_panel/manage_page")}
              className="
                rounded-lg
                border
                border-slate-300
                bg-white
                px-5
                py-2.5
                font-medium
                hover:bg-slate-100
            "
            >
              Cancel
            </button>

            <button
              type="button"
              onClick={handleSubmit}
              disabled={saving}
              className="
                rounded-lg
                bg-blue-600
                px-6
                py-2.5
                font-medium
                text-white
                transition
                hover:bg-blue-700
                disabled:opacity-50
            "
            >
              {saving
                ? "Saving..."
                : isEdit
                  ? "Update Page"
                  : "Create Page"}
            </button>

          </div>

        </div>
      </div>
    </div>
  )
}