import React from 'react'

export default function NavigationTab({ pageForm }) {

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
                        Navigation Settings
                    </h3>

                    <div className="flex items-center justify-between">

                        <div>

                            <div className="font-medium">
                                Show In Sidebar
                            </div>

                            <div className="text-sm text-slate-500">
                                Display this page in navigation menu.
                            </div>

                        </div>

                        <input
                            type="checkbox"
                            name="route.showInMenu"
                            checked={formData.route?.showInMenu}
                            onChange={handleChange}
                        />

                    </div>

                    <label className="grid gap-2">

                        <span className="text-sm font-medium">
                            Parent Menu
                        </span>

                        <input
                            name="route.parentMenu"
                            value={formData.route?.parentMenu}
                            onChange={handleChange}
                            className={fieldClass("parentMenu")}
                            placeholder="Gate"
                        />

                    </label>

                    <label className="grid gap-2">

                        <span className="text-sm font-medium">
                            Menu Order
                        </span>

                        <input
                            type="number"
                            name="route.menuOrder"
                            value={formData.route?.menuOrder}
                            onChange={handleChange}
                            className={fieldClass("menuOrder")}
                        />

                    </label>

                </div>

                {/* RIGHT */}

                <div className="space-y-6">

                    <h3 className="text-lg font-semibold">
                        Breadcrumb Settings
                    </h3>

                    <div className="flex items-center justify-between">

                        <div>

                            <div className="font-medium">
                                Show Breadcrumb
                            </div>

                            <div className="text-sm text-slate-500">
                                Display breadcrumb navigation.
                            </div>

                        </div>

                        <input
                            type="checkbox"
                            name="route.breadcrumb"
                            checked={formData.route?.breadcrumb}
                            onChange={handleChange}
                        />

                    </div>

                </div>

            </div>

        </div>

    );

}