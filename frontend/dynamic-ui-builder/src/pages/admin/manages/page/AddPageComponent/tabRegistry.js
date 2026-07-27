// tabRegistry.js

import GeneralTab from "./GeneralTab";
import NavigationTab from "./NavigationTab";
import SecurityTab from "./SecurityTab";



export const PAGE_TABS = [

    {
        key: "general",
        label: "General",
        component: GeneralTab
    },

    {
        key: "navigation",
        label: "Navigation",
        component: NavigationTab
    },

    {
        key: "security",
        label: "Security",
        component: SecurityTab
    },

    // {
    //     key: "advanced",
    //     label: "Advanced",
    //     component: AdvancedTab
    // }

];