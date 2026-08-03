import React from "react";
import { useNavigate } from "react-router-dom";
import { logout, getUsername } from "../../api/authApi";

export default function     Profile() {
  const navigate = useNavigate();

  const username = getUsername() || "Admin";
  const initial = username.charAt(0).toUpperCase();

  const handleLogout = () => {
    logout();
    navigate("/login", { replace: true });
  };

  return (
    <div className="flex items-center gap-3">
      <div className="flex h-10 w-10 items-center justify-center rounded-full bg-slate-900 text-sm font-semibold text-white">
        {initial}
      </div>

      <div className="hidden sm:block">
        <p className="text-sm font-medium text-slate-900">{username}</p>
      </div>

      <button
        onClick={handleLogout}
        className="rounded-xl border border-slate-200 bg-slate-50 px-4 py-2 text-sm font-medium text-slate-600 transition hover:bg-slate-100"
      >
        Logout
      </button>
    </div>
  );
}