import { DynamicPageSchema } from "../types/JsonSchema";

export const pageForm: DynamicPageSchema = {
  page: {
    id: 2,
    pageCode: "page_management",
    pageName: "Page Management",
    route: "/ui/page-management",
    status: "ACTIVE",
  },

  components: [
    {
      id: 1,
      name: "pageTitle",
      type: "heading",
      sequence: 1,
      properties: {
        text: "Page Management",
      },
    },

    {
      id: 2,
      name: "pageCreateCard",
      type: "card",
      sequence: 2,
      properties: {
        label: "Create Page",
        title: "Create New Page",
        description: "Create and manage dynamic UI pages.",
        width: "100%",
        style: {
          border: "1px solid #dbeafe",
          borderRadius: "14px",
          padding: "20px",
          backgroundColor: "#ffffff",
        },
      },

      children: [
        {
          id: 21,
          name: "pageName",
          type: "input",
          sequence: 1,
          properties: {
            label: "Page Name",
            placeholder: "Enter page name",
            required: true,
            width: "100%",
          },
          mapping: {
            type: "ENTITY",
            source: "UIPage.pageName",
          }
        },

        {
          id: 22,
          name: "pageCode",
          type: "input",
          sequence: 2,
          properties: {
            label: "Page Code",
            placeholder: "Enter unique page code",
            required: true,
            width: "100%",
          },
        },

        {
          id: 23,
          name: "description",
          type: "textarea",
          sequence: 3,
          properties: {
            label: "Description",
            placeholder: "Enter page description",
            width: "100%",
            height: 120,
          },
        },

        {
          id: 24,
          name: "isActive",
          type: "checkbox",
          sequence: 4,
          properties: {
            label: "Is Active",
            defaultValue: true,
          },
        },

        {
          id: 25,
          name: "savePageButton",
          type: "button",
          sequence: 5,
          properties: {
            text: "Create Page",
            style: {
              backgroundColor: "#2563eb",
              color: "#ffffff",
              padding: "10px 16px",
              borderRadius: "8px",
            },
          },
          action: [{
            event: "onClick",
            type: "SUBMIT_FORM",
            api: {
              url: "/api/admin/pages",
              method: "POST"  
            },
          }]
        },
      ],
    },

    {
      id: 3,
      name: "pageListTable",
      type: "table",
      sequence: 3,

      mapping: {
        type: "API",
        source: "PAGE_LIST_API",
        expression: "isActive = true",
      },

      properties: {
        title: "Page List",
        height: 500,
      },
    },
  ],
};