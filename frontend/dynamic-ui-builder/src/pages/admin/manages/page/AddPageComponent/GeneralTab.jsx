import React from 'react'

export default function GeneralTab({ pageForm }) {

    const {
        formData,
        handleChange,
        errors,
        isEdit
    } = pageForm;

    const fieldClass = (name) =>
        `h-11 rounded-lg border px-3 text-sm text-slate-700 outline-none transition
        ${errors?.[name]
            ? "border-red-500"
            : "border-slate-300 focus:border-blue-500"
        }`;

    return (

        <div className="space-y-8 text-black ">

            {/* ========================= */}
            {/* BASIC + ROUTING */}
            {/* ========================= */}

            <div className="grid grid-cols-2 gap-10">

                {/* LEFT */}

                <div className="space-y-5 border-r border-slate-600 pr-8">

                    <h3 className="text-lg font-semibold">
                        Basic Information
                    </h3>

                    <label className="grid gap-2">
                        <span className="text-sm font-medium">
                            Page Code <span className="text-red-500">*</span>
                        </span>

                        <input
                            name="pageCode"
                            disabled={isEdit}
                            value={formData.pageCode}
                            onChange={handleChange}
                            className={fieldClass("pageCode")}
                        />
                    </label>

                    <label className="grid gap-2">
                        <span className="text-sm font-medium">
                            Page Name <span className="text-red-500">*</span>
                        </span>

                        <input
                            name="pageName"
                            value={formData.pageName}
                            onChange={handleChange}
                            className={fieldClass("pageName")}
                        />
                    </label>

                    <label className="grid gap-2">

                        <span className="text-sm font-medium">
                            Description
                        </span>

                        <textarea
                            rows={3}
                            name="description"
                            value={formData.description}
                            onChange={handleChange}
                            className="rounded-lg border border-slate-300 p-3"
                        />

                    </label>

                    <label className="grid gap-2">

                        <span className="text-sm font-medium">
                            Version
                        </span>

                        <input
                            name="version"
                            value={formData.version}
                            onChange={handleChange}
                            className={fieldClass("version")}
                        />

                    </label>

                    <label className="grid gap-2">

                        <span className="text-sm font-medium">
                            Status
                        </span>

                        <select
                            name="status"
                            value={formData.status}
                            onChange={handleChange}
                            className={fieldClass("status")}
                        >
                            <option>DRAFT</option>
                            <option>ACTIVE</option>
                            <option>INACTIVE</option>
                            <option>DELETED</option>
                        </select>

                    </label>

                </div>

                {/* RIGHT */}

                <div className="space-y-5">

                    <h3 className="text-lg font-semibold">
                        Routing
                    </h3>

                    <label className="grid gap-2">

                        <span className="text-sm font-medium">
                            Route-path <span className="text-red-500">*</span>
                        </span>

                        <input
                            name="route.path"
                            value={formData.route?.path}
                            onChange={handleChange}
                            className={fieldClass("route")}
                        />

                    </label>

                    <label className="grid gap-2">

                        <span className="text-sm font-medium">
                            Route-Code(input only capital letters) <span className="text-red-500">*</span>
                        </span>

                        <input
                            name="route.routeCode"
                            value={formData.route?.routeCode}
                            onChange={handleChange}
                            className={fieldClass("route")}
                        />

                    </label>

                    <label className="grid gap-2">

                        <span className="text-sm font-medium">
                            Module <span className="text-red-500">*</span>
                        </span>

                        <select
                            name="moduleCode"
                            value={formData.moduleCode}
                            onChange={handleChange}
                            className={fieldClass("moduleCode")}
                        >
                            <option value="">Select Module</option>
                            <option value="YMS">YMS</option>
                        </select>

                    </label>

                    <label className="grid gap-2">

                        <span className="text-sm font-medium">
                            Category
                        </span>

                        <select
                            name="categoryCode"
                            value={formData.categoryCode}
                            onChange={handleChange}
                            className={fieldClass("categoryCode")}
                        >
                            <option value="">Select Category</option>
                            <option value="GATE">Gate</option>
                        </select>

                    </label>

                    <label className="grid gap-2">

                        <span className="text-sm font-medium">
                            Layout
                        </span>

                        <select
                            name="layoutCode"
                            value={formData.layoutCode}
                            onChange={handleChange}
                            className={fieldClass("layoutCode")}
                        >
                            <option value="">Standard Layout</option>
                            <option value="Layout1">Layout 1</option>
                        </select>

                    </label>

                </div>

            </div>

            {/* Divider */}

            <hr className="border-slate-600" />

            {/* ========================= */}
            {/* APPEARANCE */}
            {/* ========================= */}

            <div className="space-y-6">

                <h3 className="text-lg font-semibold">
                    Appearance (Beta version - not working )
                </h3>

                <div className="grid grid-cols-3 gap-8">

                    <div className="col-span-2">

                        <label className="grid gap-2">

                            <span className="text-sm font-medium">
                                Icon
                            </span>

                            <input
                                name="route.icon"
                                value={formData.route?.icon}
                                onChange={handleChange}
                                className={fieldClass("icon")}
                                placeholder="login"
                            />

                        </label>

                    </div>

                    <div>

                        <div className="rounded-lg border border-slate-300 p-5">

                            <div className="mb-3 text-sm font-medium">
                                Preview
                            </div>

                            <div className="flex h-16 items-center justify-center rounded border bg-slate-50 text-3xl">
                                ⮕
                            </div>

                        </div>

                    </div>

                </div>

            </div>

        </div>

    );

}