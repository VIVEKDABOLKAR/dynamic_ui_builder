/**
 * Creates the schema used exclusively by the live preview.
 *
 * IMPORTANT:
 * This function NEVER mutates pageJson.
 *
 * The editor changes editorDraft on every keystroke.
 * We clone the real page JSON and patch only the selected component.
 * PagePreviewPanel then passes the NEW object to DynamicPageRenderEngine.
 *
 * This is important because React/Formily must receive a new schema
 * reference whenever an editor value changes.
 */

const OPTION_TYPES = new Set(["select", "radio"]);

export function applyLiveEditToPageJson(
  pageJson: any,
  selectedComponentId: number | string | null,
  editorDraft: any
) {
  if (!pageJson || !editorDraft) {
    return pageJson;
  }

  const clone =
    typeof structuredClone === "function"
      ? structuredClone(pageJson)
      : JSON.parse(JSON.stringify(pageJson));

  /*
   * selectedComponentId is only required in EDIT mode.
   *
   * Keeping this separate also allows the preview to work safely when
   * the editor is in ADD mode.
   */
  if (
    selectedComponentId !== null &&
    selectedComponentId !== undefined
  ) {
    patchComponentById(
      clone.components,
      selectedComponentId,
      editorDraft
    );
  }

  return clone;
}

function patchComponentById(
  components: any[],
  id: number | string,
  editorDraft: any
): boolean {
  if (!Array.isArray(components)) {
    return false;
  }

  for (const node of components) {
    if (node?.id === id || String(node?.id) === String(id)) {
      patchNode(node, editorDraft);
      return true;
    }

    /*
     * Card/Layout components can contain children.
     * The previous implementation already handled children, but comparing
     * ids as both number/string makes the live editor more robust when the
     * backend returns numeric ids and the selected id comes from a form
     * value as a string.
     */
    if (
      Array.isArray(node?.children) &&
      patchComponentById(node.children, id, editorDraft)
    ) {
      return true;
    }
  }

  return false;
}

function patchNode(node: any, editorDraft: any) {
  const previousProperties = node.properties || {};
  const additional = editorDraft.additionalProperties || {};

  /*
   * Component identity.
   *
   * The converter uses node.name as the Formily property key, so changing
   * the component name must also change the name used by the preview.
   */
  node.name = editorDraft.componentName;
  node.type = editorDraft.componentType;

  if (
    editorDraft.sequenceNo !== "" &&
    editorDraft.sequenceNo !== null &&
    editorDraft.sequenceNo !== undefined
  ) {
    node.sequence = Number(editorDraft.sequenceNo);
  }

  /*
   * Start with the existing properties so fields that are not represented
   * in the editor are not accidentally removed from the preview.
   */
  node.properties = {
    ...previousProperties,

    label: editorDraft.labelName,
    placeholder: editorDraft.placeholder,

    /*
     * Keep width as a live value even when the user clears the input.
     * Clearing an editor field should mean "clear it", not "restore the
     * previous preview value".
     */
    width: editorDraft.width,

    visible: !!editorDraft.isVisible,
    disabled: !!editorDraft.isDisabled,
    required: !!editorDraft.isRequired,
  };

  /*
   * Additional Properties
   *
   * Do NOT use:
   *
   *   value || oldValue
   *
   * because that makes deletion impossible.
   *
   * If the user deletes height/border/etc. from the form, the preview must
   * also delete that value.
   */
  const additionalPropertyNames = [
    "height",
    "border",
    "borderRadius",
    "className",
  ];

  for (const key of additionalPropertyNames) {
    if (
      Object.prototype.hasOwnProperty.call(additional, key)
    ) {
      node.properties[key] = additional[key];
    }
  }

  /*
   * Preserve any future additional editor properties automatically.
   */
  for (const [key, value] of Object.entries(additional)) {
    node.properties[key] = value;
  }

  /*
   * Table uses "title", not "label".
   */
  if (editorDraft.componentType === "table") {
    node.properties.title =
      editorDraft.labelName || editorDraft.componentName;
  }

  /*
   * Button uses "text", not "label".
   */
  if (editorDraft.componentType === "button") {
    node.properties.text =
      editorDraft.labelName || editorDraft.componentName;
  }

  /*
   * Select/Radio options are generated from the CURRENT editor draft.
   *
   * Without this, adding/removing a lookup value changes the editor but
   * the preview still receives the old options from pageJson.
   */
  if (OPTION_TYPES.has(editorDraft.componentType)) {
    node.lookup = undefined;

    node.properties.options = (
      editorDraft.lookupValues || []
    ).map((item: any) => ({
      label:
        item?.displayValue ??
        item?.lookupValue ??
        "",
      value:
        item?.lookupValue ??
        item?.value ??
        "",
    }));
  }
}