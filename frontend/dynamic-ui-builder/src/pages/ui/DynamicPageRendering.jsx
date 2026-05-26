import React from 'react'
import { useEffect, useState } from 'react'
import { useLocation } from 'react-router-dom'
import { getUiPageByCode } from '../../api/uiPageApi'

export default function DynamicPageRendering() {
    //can use * in path insted of location 
  const location = useLocation()
  const [pageJson, setPageJson] = useState(null)

  // derive the full path after /ui/
  const rawPath = location.pathname || ''
  const pageCode = rawPath.startsWith('/ui/') ? rawPath.slice(4) : rawPath.replace(/^\//, '')

  useEffect(() => {
    const loadPage = async () => {
      if (!pageCode) return
      try {
        const response = await getUiPageByCode(pageCode)
        setPageJson(response)
        console.log(JSON.parse(response.jsonSchema));
      } catch (error) {
        console.error('Failed to load page json', error)
      }

    }

    loadPage()
  }, [pageCode])

  let parsedSchema = null
  try {
    parsedSchema = pageJson?.jsonSchema ? JSON.parse(pageJson.jsonSchema) : null
  } catch (error) {
    parsedSchema = null
  }

  const title = parsedSchema?.title || 'Hello World'
  const message = parsedSchema?.children?.[0]?.value || 'Hello world'

  return (
    <main className="min-h-screen bg-slate-950 px-6 py-12 text-slate-100">
      <section className="mx-auto flex min-h-[70vh] max-w-3xl items-center justify-center">
        <div className="w-full rounded-3xl border border-white/10 bg-white/5 p-8 text-center shadow-2xl shadow-slate-950/40 backdrop-blur">
          <p className="text-xs font-semibold uppercase tracking-[0.35em] text-cyan-300">Page Code: {pageCode}</p>
          <h1 className="mt-4 text-4xl font-semibold text-white">{title}</h1>
          <p className="mt-4 text-lg text-slate-300">{message}</p>
          <pre className="mt-6 overflow-auto rounded-2xl bg-slate-900/80 p-4 text-left text-xs text-slate-300">
{JSON.stringify(pageJson?.jsonSchema ? parsedSchema : { title, message }, null, 2)}
          </pre>
        </div>
      </section>

      //render json
      <div> Render json to UI </div>

      <div className="border-1 border-white/10">
            <DynamicPageRenderEngine jsonSchema={pageJson?.jsonSchema} />
      </div>
    </main>
  )
}
