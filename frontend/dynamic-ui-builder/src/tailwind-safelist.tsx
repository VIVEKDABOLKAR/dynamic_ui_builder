/**
 * This component exists only to make Tailwind generate utility classes.
 * Import it once in App.tsx.
 */

export default function TailwindSafelist() {
  const colors = [
    "slate",
    "gray",
    "zinc",
    "neutral",
    "stone",
    "red",
    "orange",
    "amber",
    "yellow",
    "lime",
    "green",
    "emerald",
    "teal",
    "cyan",
    "sky",
    "blue",
    "indigo",
    "violet",
    "purple",
    "fuchsia",
    "pink",
    "rose",
  ];

  const shades = [
    "50",
    "100",
    "200",
    "300",
    "400",
    "500",
    "600",
    "700",
    "800",
    "900",
    "950",
  ];

  return (
    <div className="h-2 w-2 border flex flex-col">
      {colors.map((color) =>
        shades.map((shade) => (
          <div
            key={`${color}-${shade}`}
            className={`
              bg-${color}-${shade}
              text-${color}-${shade}
              border-${color}-${shade}
              ring-${color}-${shade}
              fill-${color}-${shade}
              stroke-${color}-${shade}
              hover:bg-${color}-${shade}
              hover:text-${color}-${shade}
              focus:bg-${color}-${shade}
              focus:text-${color}-${shade}
            `}
          />
        ))
      )}
    </div>
  );
}