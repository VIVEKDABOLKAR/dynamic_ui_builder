import { DynamicPageSchema } from "../types/JsonSchema";

export const pageForm: DynamicPageSchema = {
  page: {
    id: 2,
    pageCode: "page_management",
    pageName: "Page Management",
    route: "/ui/page-management",
    status: "ACTIVE",
  },

  actions: {
    savePage: {
      type: "SUBMIT_FORM",
      api: {
        url: "/api/test",
        method: "POST",
      },
    },
    navHome: {
      type: "NAVIGATE",
      navigate: {
        path: "/"
      }
    }
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
        // ============================================
        // ROW 1
        // ============================================

        {
          id: 100,
          name: "pageBasicInfoRow",
          type: "layout",
          sequence: 1,

          properties: {
            direction: "row",
            gap: 20,
            justifyContent: "space-between",
            alignItems: "center",
            width: "100%",
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
              },

              mapping: {
                type: "ENTITY",
                source: "UIPage.pageName",
              },
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
              },

              mapping: {
                type: "ENTITY",
                source: "UIPage.pageCode",
              },
            },
          ],
        },

        // ============================================
        // ROW 2
        // ============================================

        {
          id: 101,
          name: "descriptionRow",
          type: "layout",
          sequence: 2,

          properties: {
            direction: "column",
            gap: 10,
            width: "100%",
          },

          children: [
            {
              id: 23,
              name: "description",
              type: "textarea",
              sequence: 1,

              properties: {
                label: "Description",
                placeholder: "Enter page description",
                width: "100%",
                height: 120,
              },

              mapping: {
                type: "ENTITY",
                source: "UIPage.description",
              },
            },
          ],
        },

        // ============================================
        // ROW 3
        // ============================================

        {
          id: 102,
          name: "actionRow",
          type: "layout",
          sequence: 3,

          properties: {
            direction: "row",
            gap: 16,
            justifyContent: "space-between",
            alignItems: "center",
            width: "100%",
            marginTop: 20,
          },

          children: [
            {
              id: 24,
              name: "isActive",
              type: "checkbox",
              sequence: 1,

              properties: {
                label: "Is Active",
                defaultValue: true,
              },

              mapping: {
                type: "ENTITY",
                source: "UIPage.isActive",
              },
            },

            {
              id: 25,
              name: "savePageButton",
              type: "button",
              sequence: 2,

              properties: {
                text: "Create Page",

                style: {
                  backgroundColor: "#2563eb",
                  color: "#ffffff",
                  padding: "10px 16px",
                  borderRadius: "8px",
                },
              },

              action: [
                {
                  event: "onClick",
                  ref: "savePage",
                  condition: "true",
                },
                // {
                //   event: "onClick",
                //   ref: "navHome",
                //   condition: "true",
                // }
              ],
            },

            
          ],
          
        },

        {
              id: 26,
              name: "datepickercustom",
              type: "datepicker",
              sequence: 2,

              properties: {
                text: "Create Page",
                label: "page create date",

                style: {
                  backgroundColor: "#2563eb",
                  color: "#ffffff",
                  padding: "10px 16px",
                  borderRadius: "8px",
                },

              },
              mapping: {
                type: "ENTITY",
                source: "UIPage.date",
              },
            },
      ],
    },

    // ============================================
    // PAGE LIST TABLE
    // ============================================

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
        label: "Page List",
        placeholder: "All configured pages appear in this table.",
        height: 500,
      },
    },
  ],
};