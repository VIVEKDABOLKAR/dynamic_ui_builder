import React from 'react'
import { useEffect, useState } from 'react'
import { useLocation } from 'react-router-dom'
import { getUiPageByCode } from '../../api/uiPageApi'
import DynamicPageRenderEngine from '../../components/dynamicPageRender/DynamicPageRenderEngine'
import { basicFormSchema } from '../../components/dynamicPageRender/examples/basicForm'
import { pageForm } from '../../components/dynamicPageRender/examples/pageForm'

export default function DynamicPage() {
    //can use * in path insted of location 
    const location = useLocation()
    const [pageJson, setPageJson] = useState(null)

    // derive the full path after /ui/
    const rawPath = location.pathname || ''
    const pageCode = rawPath.startsWith('/ui_demo/') ? rawPath.slice(9) : rawPath.replace(/^\//, '')

    useEffect(() => {
        const loadPage = async () => {
            if (!pageCode) return
            console.log(pageCode)

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
        parsedSchema = pageJson?.jsonSchema ? JSON.parse(pageJson.jsonSchema) : pageForm
    } catch (error) {
        parsedSchema = pageForm
    }

    const title = parsedSchema?.title || 'Hello World'
    const message = parsedSchema?.children?.[0]?.value || 'Hello world'

    if(!parsedSchema) {
        return (
            <>
                <h1>Loading</h1>
            </>
        )
    }
    return (
        <>
            <DynamicPageRenderEngine jsonSchema={parsedSchema} className="m-4 p-4" />
        </>
    )
}
