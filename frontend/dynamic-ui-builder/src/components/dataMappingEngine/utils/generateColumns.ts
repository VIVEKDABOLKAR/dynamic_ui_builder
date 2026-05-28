export interface DynamicRows {
    field: string,
    headerName: string,
    sortable: boolean,
    filter: boolean,
    resizable: boolean,
}

export function generateColumns(
  rows: any[]
):DynamicRows[] {

  if (!rows || rows.length === 0) {
    return [];
  }

  // first object keys
  const firstRow = rows[0];

  return Object.keys(firstRow).map((key) => {

    return {

      field: key,

      headerName: formatHeader(key),

      sortable: true,

      filter: true,

      resizable: true
    };
  });
}

function formatHeader(
  text: string
) {

  return text

    // pageName -> page Name
    .replace(/([A-Z])/g, " $1")

    // capitalize first
    .replace(/^./, (str) => str.toUpperCase());
}