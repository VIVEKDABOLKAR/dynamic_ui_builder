import { DynamicPageSchema } from "../types/JsonSchema";

export const basicFormSchema: DynamicPageSchema = {
  page: {
    id: 1,
    pageCode: "employee_onboarding",
    pageName: "Employee Onboarding",
    route: "/ui/employee_onboarding",
    status: "ACTIVE",
  },
  components: [
    {
      id: 1,
      name: "pageTitle",
      type: "heading",
      sequence: 1,
      properties: {
        text: "Employee Onboarding Form",
      },
    },
    {
      id: 2,
      name: "employeeDetailsCard",
      type: "card",
      sequence: 2,
      properties: {
        label: "Employee Details",
        title: "Employee Details",
        description: "Capture the main information for the new employee.",
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
          name: "firstName",
          type: "input",
          sequence: 1,
          properties: {
            label: "First Name",
            placeholder: "Enter first name",
            required: true,
            width: "100%",
          },
        },
        {
          id: 22,
          name: "lastName",
          type: "input",
          sequence: 2,
          properties: {
            label: "Last Name",
            placeholder: "Enter last name",
            required: true,
            width: "100%",
          },
        },
        {
          id: 23,
          name: "email",
          type: "input",
          sequence: 3,
          properties: {
            label: "Email Address",
            placeholder: "name@company.com",
            required: true,
            width: "100%",
          },
        },
        {
          id: 24,
          name: "bio",
          type: "textarea",
          sequence: 4,
          properties: {
            label: "Short Bio",
            placeholder: "Write a short bio",
            height: 120,
            width: "100%",
          },
        },
      ],
    },
    {
      id: 3,
      name: "workInfoCard",
      type: "card",
      sequence: 3,
      properties: {
        label: "Work Information",
        title: "Work Information",
        description: "Set the employee role and access preferences.",
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
          id: 31,
          name: "department",
          type: "select",
          sequence: 1,
          properties: {
            label: "Department",
            placeholder: "Select department",
            options: [
              { label: "Engineering", value: "engineering" },
              { label: "Sales", value: "sales" },
              { label: "HR", value: "hr" },
            ],
            width: "100%",
          },
        },
        {
          id: 32,
          name: "role",
          type: "select",
          sequence: 2,
          properties: {
            label: "Role",
            placeholder: "Select role",
            options: [
              { label: "Manager", value: "manager" },
              { label: "Developer", value: "developer" },
              { label: "Analyst", value: "analyst" },
            ],
            width: "100%",
          },
        },
        {
          id: 33,
          name: "gender",
          type: "radio",
          sequence: 3,
          properties: {
            label: "gender",
            placeholder: "Select gender",
            options: [
              { label: "Male", value: "male" },
              { label: "Women", value: "women" },
            ],
            width: "100%",
          },
        },
        {
          id: 34,
          name: "isActive",
          type: "checkbox",
          sequence: 4,
          properties: {
            label: "Active employee",
            defaultValue: true,
          },
        },
      ],
    },
    {
      id: 4,
      name: "saveButton",
      type: "button",
      sequence: 4,
      properties: {
        text: "Create Employee",
        style: {
          backgroundColor: "#2563eb",
          color: "#ffffff",
          padding: "10px 16px",
          borderRadius: "8px",
        },
      },
    },
    {
      "id": 50,
      "name": "pagesTable",
      "type": "table",
      "sequence": 5,
      "mapping": {
        "type": "API",
        "source": "PAGE_LIST_API",
        "expression": "status = 'ACTIVE'"
      },
      "properties": {
        "title": "Pages List",
        "height": 400
      }
    }
  ],
};
