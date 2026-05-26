import React from 'react'
import { convertToFormilySchema } from './JsonConvert.ts';



/**
 * this is the core engine to render dynamic page based on json schema
 * it will take json code(our defined schema)
 *       so first it will send json code to converter 
 *                 which will convert our json custome schma to formaily json schema
 *       then it will send formaily json schema to renderer
 *                 which will render the page based on formaily json schema
 * @param {*} param0 
 * @returns 
 */
export default function DynamicPageRenderEngine({ jsonSchema }) {

    //get formily schema from json schema
    const formilySchema = convertToFormilySchema(jsonSchema)

    console.log(formilySchema)

    // const form = createForm();
    return (
        <>
            <div>DynamicPageRenderEngine</div>

            {/* <div>
                <FormProvider form={form}>
                    <SchemaField schema={formilySchema} />
                </FormProvider>
            </div> */}

        </>
    )
}
