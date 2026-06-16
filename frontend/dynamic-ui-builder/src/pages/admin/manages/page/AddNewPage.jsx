import React, { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { createPage, getPageByCode, updatePage } from '../../../../api/adminPageApi'

export default function AddNewPage() {
  const navigate = useNavigate()
  const { pageCode } = useParams()
  const isEdit = Boolean(pageCode)
  const [formData, setFormData] = useState({
    pageName: '',
    pageCode: '',
    description: '',
    isActive: true,
  })
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [message, setMessage] = useState('')

  useEffect(() => {
    if (!isEdit) return

    const fetchPage = async () => {
      try {
        const page = await getPageByCode(pageCode)
        setFormData({
          pageName: page.pageName || '',
          pageCode: page.pageCode || pageCode,
          description: page.description || '',
          isActive: page.isActive ?? true,
        })
      } catch (error) {
        console.error('Failed to load page for edit', error)
        setMessage('Failed to load page data.')
      }
    }

    fetchPage()
  }, [isEdit, pageCode])

  const handleChange = (event) => {
    const { name, value, type, checked } = event.target

    setFormData((current) => ({
      ...current,
      [name]: type === 'checkbox' ? checked : value,
    }))
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    if (!formData.pageName || !formData.pageCode || !formData.description) {
      setMessage('Please fill in all required fields before saving.')
      return
    }

    setIsSubmitting(true)
    setMessage('')

    try {
      if (isEdit) {
        await updatePage(pageCode, {
          pageName: formData.pageName,
          description: formData.description,
          isActive: formData.isActive,
        })
        setMessage('Page updated successfully.')
      } else {
        await createPage(formData)
        setMessage('Page created successfully.')
      }
      navigate('/admin_panel/manage_page')
    } catch (error) {
      setMessage(error.response?.data?.message || error.message || 'Something went wrong.')
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <div className="max-w ">
      <div className="mb-6 flex items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-semibold text-slate-900">{isEdit ? 'Edit Page' : 'Add New Page'}</h1>
          <p className="mt-1 text-sm text-slate-500">{isEdit ? 'Update this UI page.' : 'Create a new UI page using the backend API.'}</p>
        </div>

        <Link
          to="/admin_panel/manage_page"
          className="rounded-full border border-slate-200 bg-white px-4 py-2 text-sm font-medium text-slate-700 shadow-sm transition hover:bg-slate-50"
        >
          Back
        </Link>
      </div>

      <form onSubmit={handleSubmit} className="grid gap-5 rounded-2xl border border-slate-200 bg-white p-5 shadow-sm sm:p-6">
        <div className="grid gap-4 sm:grid-cols-2">
          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Page Name
            <input
              type="text"
              name="pageName"
              value={formData.pageName}
              onChange={handleChange}
              required
              className="rounded-xl border border-slate-300 px-4 py-3 outline-none transition focus:border-cyan-400"
              placeholder="Dashboard Page"
            />
          </label>

          <label className="grid gap-2 text-sm font-medium text-slate-700">
            Page Code
            <input
              type="text"
              name="pageCode"
              value={formData.pageCode}
              onChange={handleChange}
              required
              disabled={isEdit}
              className="rounded-xl border border-slate-300 px-4 py-3 outline-none transition focus:border-cyan-400 disabled:cursor-not-allowed disabled:bg-slate-100"
              placeholder="dashboard_page"
            />
          </label>
        </div>

        <label className="grid gap-2 text-sm font-medium text-slate-700">
          Description
          <textarea
            name="description"
            value={formData.description}
            onChange={handleChange}
            rows={4}
            required
            className="rounded-xl border border-slate-300 px-4 py-3 outline-none transition focus:border-cyan-400"
            placeholder="Short page description"
          />
        </label>

        <label className="flex items-center gap-3 text-sm font-medium text-slate-700">
          <input
            type="checkbox"
            name="isActive"
            checked={formData.isActive}
            onChange={handleChange}
            className="h-4 w-4 rounded border-slate-300 text-cyan-500 focus:ring-cyan-400"
          />
          Active
        </label>

        {message ? (
          <p className="rounded-xl bg-slate-50 px-4 py-3 text-sm text-slate-600">{message}</p>
        ) : null}

        <div className="flex items-center gap-3">
          <button
            type="submit"
            disabled={isSubmitting}
            className="rounded-full bg-slate-950 px-5 py-3 text-sm font-semibold text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {isSubmitting ? 'Saving...' : isEdit ? 'Update Page' : 'Create Page'}
          </button>
        </div>
      </form>
    </div>
  )
}
