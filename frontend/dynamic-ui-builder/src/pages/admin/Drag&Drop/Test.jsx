import { toast } from "react-toastify";
import React from 'react'

export default function Test() {
    return (
        <>
            <div>test</div>
            <button
                onClick={() => toast.success("Working")}
            >
                Test
            </button>
        </>
    )
}
