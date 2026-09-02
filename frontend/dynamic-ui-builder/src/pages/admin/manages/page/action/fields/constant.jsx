export const Field = ({ label, children }) => (
  <div className="grid gap-2">
    <label className="text-sm font-medium text-slate-700">
      {label}
    </label>
    {children}
  </div>
);

export const inputClassName =
  "rounded-xl border border-slate-200 px-3 py-2 text-sm outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100";

export const textareaClassName =
  "min-h-24 rounded-xl border border-slate-200 px-3 py-2 font-mono text-sm outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100";
