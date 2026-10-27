import React from 'react';
import { LayoutDashboard, Users, Building, BarChart3, Database, Code, BookOpen } from 'lucide-react';

export default function Sidebar({ activeTab, setActiveTab }) {
  const menuItems = [
    { id: 'dashboard', label: 'Dashboard', icon: LayoutDashboard },
    { id: 'employees', label: 'Employee Directory', icon: Users },
    { id: 'analytics', label: 'Analytics & Reports', icon: BarChart3 },
  ];

  return (
    <aside className="hidden lg:flex lg:flex-col w-64 bg-slate-900 text-slate-300 border-r border-slate-800 shrink-0 select-none">
      {/* Brand Header */}
      <div className="p-6 border-b border-slate-800/80">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-indigo-600 to-indigo-400 flex items-center justify-center text-white shadow-md shadow-indigo-500/20">
            <Users className="w-5 h-5" />
          </div>
          <div>
            <h2 className="text-base font-bold text-white tracking-wide">
              StaffPortal
            </h2>
            <span className="text-xs text-indigo-400 font-medium tracking-wider uppercase">
              Enterprise Suite
            </span>
          </div>
        </div>
      </div>

      {/* Main Navigation */}
      <nav className="flex-1 p-4 space-y-1.5">
        <div className="px-3 pb-2 text-[11px] font-semibold tracking-wider uppercase text-slate-400">
          Navigation
        </div>
        {menuItems.map((item) => {
          const Icon = item.icon;
          const isActive = activeTab === item.id;
          return (
            <button
              key={item.id}
              onClick={() => setActiveTab(item.id)}
              className={`w-full flex items-center gap-3 px-3.5 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                isActive
                  ? 'bg-indigo-600 text-white shadow-sm'
                  : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
              }`}
            >
              <Icon className="w-4 h-4 shrink-0" />
              <span>{item.label}</span>
            </button>
          );
        })}

        <div className="pt-6 px-3 pb-2 text-[11px] font-semibold tracking-wider uppercase text-slate-400">
          Developer Tools
        </div>
        <a
          href="http://localhost:8080/swagger-ui.html"
          target="_blank"
          rel="noopener noreferrer"
          className="w-full flex items-center gap-3 px-3.5 py-2.5 rounded-lg text-sm font-medium text-slate-400 hover:text-slate-200 hover:bg-slate-800/60 transition-colors"
        >
          <BookOpen className="w-4 h-4 shrink-0 text-slate-400" />
          <span>Swagger OpenAPI</span>
        </a>
        <a
          href="http://localhost:8080/actuator/health"
          target="_blank"
          rel="noopener noreferrer"
          className="w-full flex items-center gap-3 px-3.5 py-2.5 rounded-lg text-sm font-medium text-slate-400 hover:text-slate-200 hover:bg-slate-800/60 transition-colors"
        >
          <Code className="w-4 h-4 shrink-0 text-slate-400" />
          <span>Actuator Health</span>
        </a>
      </nav>

      {/* Bottom Stack Badge */}
      <div className="p-4 border-t border-slate-800/80 bg-slate-950/40">
        <div className="rounded-lg bg-slate-800/60 p-3 border border-slate-800 text-xs">
          <div className="flex items-center gap-2 text-indigo-400 font-semibold mb-1">
            <Database className="w-3.5 h-3.5" />
            <span>Tech Stack Specs</span>
          </div>
          <div className="space-y-1 text-slate-400">
            <p>Spring Boot 3.3.5 / Java 17+</p>
            <p>PostgreSQL Database</p>
            <p>React 18 + Tailwind CSS</p>
          </div>
        </div>
      </div>
    </aside>
  );
}
