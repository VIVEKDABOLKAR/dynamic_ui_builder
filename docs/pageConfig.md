# Dynamic UI Framework - Page Metadata Design (Version 1.0)

## Objective

The goal of the page metadata is **not to describe the UI components**, but to describe everything required to load, secure, navigate, and render a page within the framework.

The schema should remain generic so that the framework can support YMS, WMS, TMS, HRMS, CRM, or any future application.

---

# Design Principles

* Page metadata should only contain **page-level configuration**.
* Business logic should never be stored inside page metadata.
* Role management should be completely independent of page definitions.
* Components should define required permissions, not roles.
* Layouts should be reusable and shared across multiple pages.

---

# Proposed Page Metadata

```typescript
export interface PageMeta {

  id: number;

  pageCode: string;

  pageName: string;

  description?: string;

  route: string;

  module: string;

  category?: string;

  version?: string;

  icon?: string;

  layout: string;

  status: PageStatus;

  security: PageSecurityConfig;

  navigation: NavigationConfig; // use it for sidebar 

  tags?: string[]; // not neccesary
}
```

---

# Security Configuration

```typescript
export interface PageSecurityConfig {

    requireAuthentication: boolean;

    permission: string;

}
```

Example

```json
{
    "security": {
        "requireAuthentication": true,
        "permission": "gate:checkin:view"
    }
}
```

The page only specifies the permission required to access it.

It does **not** specify any roles.

---

# Navigation Configuration

```typescript
export interface NavigationConfig {

    showInMenu: boolean;

    parentMenu?: string;

    menuOrder?: number;

    breadcrumb?: boolean;
}
```

Example

```json
{
    "navigation": {
        "showInMenu": true,
        "parentMenu": "Gate",
        "menuOrder": 2,
        "breadcrumb": true
    }
}
```

---

---

# Example Page Metadata

```json
{


    "page": {

        "id": 101,

        "pageCode": "GATE_CHECKIN",

        "pageName": "Gate Check-In",

        "description": "Truck Gate Entry",

        "route": "/gate/check-in",

        "module": "YMS",

        "category": "Gate",

        "version": 1,

        "icon": "login",

        "layout": "standard-layout",

        "status": "ACTIVE",

        "security": {
            "requireAuthentication": true,
            "permission": "gate:checkin:view"
        },

        "navigation": {
            "showInMenu": true,
            "parentMenu": "Gate",
            "menuOrder": 1,
            "breadcrumb": true
        },


        "tags": [
            "gate",
            "checkin",
            "truck"
        ]
    }
}
```

---

# Layout Strategy

The page metadata should only contain a **layout identifier**.

Example

```json
{
    "layout": "standard-layout"
}
```

The actual layout definition should exist separately inside a Layout Registry.

Example

```
StandardLayout

├── Navbar
├── Sidebar
├── Breadcrumb
├── Footer
└── Content Area
```

The renderer will

```
Load Page

↓

Read Page Metadata

↓

Resolve Layout

↓

Render Layout

↓

Inject Page Components
```

This allows changing the sidebar, header, or footer for hundreds of pages by updating only one layout.

---

# Security Architecture

The framework should implement three independent security layers.

## 1. Page Security

Determines whether a user can access a page.

Example

```
gate:checkin:view
```

---

## 2. Component Security

Each component can define its own permission.

Example

```
Approve Button

↓

gate:approve
```

```
Schedule Arrival

↓

appointment:schedule
```

During rendering

```
User Permission

↓

Compare

↓

Render / Hide / Disable
```

The component never stores roles.

---

## 3. API Security

Every backend API must validate permissions independently.

Even if someone bypasses the UI, the API should reject unauthorized operations.

---

# Why Roles Are Not Stored in Page JSON

Roles are business-specific and may change between customers.

Example

Customer A

```
Gate Operator

↓

gate:approve
```

Customer B

```
Security Supervisor

↓

gate:approve
```

Customer C

```
Gate Manager

↓

gate:approve
```

The page should not change because of these business decisions.

The page only declares

```
Required Permission

↓

gate:approve
```

---

# Future Handling of Role-Permission Relationships

The framework should manage permissions using a centralized authorization model.

```
User

↓

Role

↓

Permissions

↓

Page / Component / API
```

Example

```
ADMIN

↓

gate:checkin:view
gate:approve
appointment:schedule
truck:create
truck:update
```

```
GATE_OPERATOR

↓

gate:checkin:view
gate:approve
```

```
YARD_MANAGER

↓

gate:checkin:view
appointment:schedule
truck:view
```

If a customer wants to change access rules, only the Role-Permission mapping changes.

The page JSON remains unchanged.

---

# Facility-Specific Role Mapping

Different facilities may use different role structures.

Example

Facility A

```
Gate Operator

↓

gate:approve
```

Facility B

```
Security Officer

↓

gate:approve
```

Facility C

```
Shift Supervisor

↓

gate:approve
appointment:schedule
```

The framework should support this through database configuration.

Example

```
Facility

↓

Roles

↓

Permissions
```

Suggested tables

```
Facility

Role

Permission

FacilityRole

RolePermission

UserRole
```

The runtime authorization flow becomes

```
Login

↓

Current Facility

↓

Assigned Roles

↓

Resolve Permissions

↓

Load User Permissions

↓

Render Page

↓

Render Components

↓

Validate APIs
```

This design allows every facility to define its own roles without requiring any changes to page JSON or application code.

---

# Final Decision

* Page JSON will contain **page metadata only**.
* Pages define **required permissions**, not roles.
* Components define **component-level permissions**, not roles.
* Layouts are managed through a separate Layout Registry.
* Role-to-Permission mapping is maintained centrally in the authorization module.
* Facilities can customize role-permission relationships through configuration without affecting page definitions.
