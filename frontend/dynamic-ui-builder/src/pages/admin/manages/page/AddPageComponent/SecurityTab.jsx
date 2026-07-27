import React from "react";

export default function SecurityTab({ pageForm }) {

    const {
        formData,
        handleChange,
        errors
    } = pageForm;

    const fieldClass = (name) =>
        `h-11 rounded-lg border px-3 text-sm text-slate-700 outline-none transition
        ${errors?.[name]
            ? "border-red-500"
            : "border-slate-300 focus:border-blue-500"
        }`;

    return (

        <div className="space-y-8 text-black">

            <div className="grid grid-cols-2 gap-10">

                {/* LEFT */}

                <div className="space-y-6 border-r border-slate-600 pr-8">

                    <h3 className="text-lg font-semibold">
                        Security Settings (Beta-version)
                    </h3>

                    <div className="flex items-center justify-between">

                        <div>

                            <div className="font-medium">
                                Require Authentication
                            </div>

                            <div className="text-sm text-slate-500">
                                User must login before accessing this page.
                            </div>

                        </div>

                        <input
                            type="checkbox"
                            name="requireAuthentication"
                            checked={formData.requireAuthentication}
                            onChange={handleChange}
                        />

                    </div>

                    <label className="grid gap-2">

                        <span className="text-sm font-medium">
                            Required Permission
                        </span>

                        <input
                            name="permissionCode"
                            value={formData.permissionCode}
                            onChange={handleChange}
                            placeholder="gate:checkin:view"
                            className={fieldClass("permissionCode")}
                        />

                    </label>

                </div>

                {/* RIGHT */}

                <div className="space-y-6">

                    <h3 className="text-lg font-semibold">
                        Permission Details
                    </h3>

                    <div className="rounded-lg border border-slate-300 p-5">

                        <div className="mb-3 font-semibold">
                            Permission Preview
                        </div>

                        <div className="space-y-2 text-sm">

                            <div>
                                <span className="font-medium">Code :</span>{" "}
                                {formData.permissionCode || "-"}
                            </div>

                            <div className="text-slate-500">
                                This permission will be checked before rendering
                                the page.
                            </div>

                        </div>

                    </div>

                </div>

            </div>

        </div>

    );

}