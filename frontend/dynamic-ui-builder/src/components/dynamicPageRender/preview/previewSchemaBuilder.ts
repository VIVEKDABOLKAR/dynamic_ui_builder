/**
 * Live preview now reuses the SAME assembled page JSON that real pages
 * render from (GET /api/ui/pages/{pageCode}, kept in sync with the
 * component table by the backend's syncPageJson on every save) instead of
 * reconstructing a separate, simplified schema locally.
 *
 * This file's only job is to patch the ONE component currently being
 * edited on top of that fetched JSON, so keystrokes in the editor show up
 * immediately without waiting for Add/Update to persist.
 */

const OPTION_TYPES = new Set(["select", "radio"]);

/**
 * Returns a new page JSON with the node matching `selectedComponentId`
 * patched with the live values from `editorDraft`. Leaves everything else
 * (mapping, actions, other components, page metadata) untouched, so the
 * preview stays as close to the real rendered page as possible.
 *
 * A new (not-yet-saved) component in Add mode isn't patched in here —
 * per the current flow, it only appears in the preview once it has been
 * added and the page JSON has been refetched.
 */
export function applyLiveEditToPageJson(pageJson, selectedComponentId, editorDraft) {
  if (!pageJson || !selectedComponentId || !editorDraft) return pageJson;

  const clone =
    typeof structuredClone === "function"
      ? structuredClone(pageJson)
      : JSON.parse(JSON.stringify(pageJson));

  patchComponentById(clone.components, selectedComponentId, editorDraft);
  return clone;
}

function patchComponentById(components, id, editorDraft) {
  if (!Array.isArray(components)) return false;

  for (const node of components) {
    if (node?.id === id) {
      patchNode(node, editorDraft);
      return true;
    }
    if (Array.isArray(node?.children) && patchComponentById(node.children, id, editorDraft)) {
      return true;
    }
  }
  return false;
}

// Only touches the fields the backend's toComponentNode actually writes
// into the real schema (see UIPageJsonServiceImp), so the preview never
// implies support the production render path doesn't have.
function patchNode(node, editorDraft) {
  node.name = editorDraft.componentName;
  node.type = editorDraft.componentType;
  node.sequence = Number(editorDraft.sequenceNo) || node.sequence;

  node.properties = {
    ...node.properties,
    label: editorDraft.labelName,
    placeholder: editorDraft.placeholder,
    visible: editorDraft.isVisible,
    disabled: editorDraft.isDisabled,
    required: editorDraft.isRequired,
  };

  if (editorDraft.componentType === "button") {
    node.properties.text = editorDraft.labelName;
  }

  if (OPTION_TYPES.has(editorDraft.componentType)) {
    // The real schema points select/radio at a lookup API for options.
    // While editing, reflect unsaved lookup-value edits inline instead —
    // a live network fetch wouldn't see them until they're saved anyway.
    delete node.lookup;
    node.properties.options = (editorDraft.lookupValues || []).map((item) => ({
      label: item.displayValue || item.lookupValue,
      value: item.lookupValue,
    }));
  }
}
