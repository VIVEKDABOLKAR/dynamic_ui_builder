import React from 'react'
import { useEffect, useState } from 'react'
import { useLocation } from 'react-router-dom'
import { getUiPageByCode } from '../../api/uiPageApi'
import { resolveRoute } from '../../api/routeApi'
import DynamicPageRenderEngine from '../../components/dynamicPageRender/DynamicPageRenderEngine'
import { basicFormSchema } from '../../components/dynamicPageRender/examples/basicForm'
import { pageForm } from '../../components/dynamicPageRender/examples/pageForm'
import { useFacility } from '../../context/FacilityV2Context'

export default function DynamicPage() {
    //can use * in path insted of location 
    const location = useLocation()
    const [pageJson, setPageJson] = useState(null)
    const [loading, setLoading] = useState(false);
    const [accessDenied, setAccessDenied] = useState(false);

    const { selectedFacility } = useFacility();

    // derive the full path after /ui/
    const rawPath = location.pathname || ''
    const pathParams = rawPath.startsWith('/ui_demo/') ? rawPath.slice(8) : rawPath.replace(/^\//, '')

    useEffect(() => {
        if (!selectedFacility?.id) return;

        setLoading(true);
        setAccessDenied(false);
        const loadPage = async () => {
            //load pageCode based on route path
            let pageCode
            try {
                const response = await resolveRoute(pathParams)
                pageCode = response.pageCode
            } catch (error) {
                console.error('Failed to Resolve route to page')
                if (error?.response?.status === 403) {
                    setAccessDenied(true)
                    setLoading(false)
                    return
                }
            }

            if (!pageCode) {
                setPageJson("")
                setLoading(false)
                return
            }

            console.log(pageCode)

            try {
                const response = await getUiPageByCode(pageCode)
                setPageJson(response)
                console.log(response);
            } catch (error) {
                console.error('Failed to load page json', error)
                if (error?.response?.status === 403) {
                    setAccessDenied(true)
                    setLoading(false)
                    return
                }
            } finally {
                setLoading(false);
            }
        }

        loadPage()
    }, [pathParams, selectedFacility?.id])

    let parsedSchema = null
    try {
        parsedSchema = pageJson?.jsonSchema ? pageJson.jsonSchema : null
    } catch (error) {
        console.log(error)
        parsedSchema = basicFormSchema
    }

    const title = parsedSchema?.title || 'Hello World'
    const message = parsedSchema?.children?.[0]?.value || 'Hello world'

    if (loading) {
        return (
            <div className="flex h-dvh items-center justify-center">
                <h1 className="text-lg font-medium">Loading...</h1>
            </div>
        )
    }

    if (accessDenied) {
        return (
            <div className="flex h-dvh flex-col items-center justify-center gap-2">
                <h1 className="text-lg font-semibold text-red-600">Access Denied</h1>
                <p className="text-sm text-gray-500">Your role or facility does not have permission to view this page.</p>
            </div>
        )
    }

    console.log("this schema is rendering....", parsedSchema)
    return (
        <>
            <DynamicPageRenderEngine jsonSchema={parsedSchema} className="m-4 p-4" />
        </>
    )
}
