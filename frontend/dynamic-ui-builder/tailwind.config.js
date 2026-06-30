/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],

  safelist: [
    // background colors
    {
      pattern: /bg-(red|blue|green|yellow|gray)-(100|200|300|400|500|600|700|800|900)/,
    },

    // padding
    {
      pattern: /p-(1|2|3|4|5|6|8|10)/,
    },

    // margin
    {
      pattern: /m-(1|2|3|4|5|6|8|10)/,
    },

    // rounded
    {
      pattern: /rounded-(sm|md|lg|xl)/,
    },

    // text colors
    {
      pattern: /text-(red|blue|green|gray)-(500|600|700|800)/,
    },
  ],
};