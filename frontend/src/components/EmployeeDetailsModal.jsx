import React from 'react';
import { X, Mail, Building, DollarSign, Calendar, Edit2, ShieldCheck } from 'lucide-react';

export default function EmployeeDetailsModal({
  isOpen,
  onClose,
  employee,
  onEdit,
  currency
}) {
  if (!isOpen || !employee) return null;

  const initials = employee.name
    ? employee.name
        .split(' ')
        .map((n) => n[0])
        .slice(0, 2)
        .join('')
        .toUpperCase()
    : 'EM';

  const formatMoney = (val) => {
    if (val === null || val === undefined) return `${currency.symbol}0.00`;
    return `${currency.symbol}${Number(val).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
  };

  const formatDateTime = (isoString) => {
    if (!isoString) return 'Not recorded';
    try {
      const d = new Date(isoString);
      return d.toLocaleString(undefined, {
        dateStyle: 'medium',
        timeStyle: 'short'
      });
    } catch {
      return isoString;
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/50 backdrop-blur-sm animate-fadeIn">
      <div className="bg-white rounded-2xl shadow-xl border border-slate-200 w-full max-w-md overflow-hidden transition-all scale-100">
        {/* Header Cover */}
        <div className="h-24 bg-gradient-to-r from-indigo-600 to-indigo-700 relative p-4 flex justify-end">
          <button
            onClick={onClose}
            className="p-1 rounded-full text-white/80 hover:text-white hover:bg-white/10 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Profile Card Body */}
        <div className="px-6 pb-6 relative">
          {/* Avatar */}
          <div className="-mt-12 mb-4 flex justify-between items-end">
            <div className="w-20 h-20 rounded-2xl bg-white p-1 shadow-md">
              <div className="w-full h-full rounded-xl bg-indigo-100 text-indigo-700 flex items-center justify-center text-xl font-bold">
                {initials}
              </div>
            </div>
            <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
              <ShieldCheck className="w-3.5 h-3.5" /> Active Staff
            </span>
          </div>

          <div>
            <h2 className="text-xl font-bold text-slate-900">{employee.name}</h2>
            <p className="text-xs font-mono text-slate-400">Employee ID: #{employee.id}</p>
          </div>

          {/* Details Grid */}
          <div className="mt-5 space-y-3 divide-y divide-slate-100 text-sm">
            <div className="pt-2 flex items-center justify-between">
              <span className="text-slate-500 text-xs flex items-center gap-2">
                <Mail className="w-4 h-4 text-slate-400" /> Corporate Email
              </span>
              <span className="font-medium text-slate-800">{employee.email}</span>
            </div>

            <div className="pt-3 flex items-center justify-between">
              <span className="text-slate-500 text-xs flex items-center gap-2">
                <Building className="w-4 h-4 text-slate-400" /> Department
              </span>
              <span className="font-semibold px-2 py-0.5 rounded text-xs bg-slate-100 text-slate-800">
                {employee.department}
              </span>
            </div>

            <div className="pt-3 flex items-center justify-between">
              <span className="text-slate-500 text-xs flex items-center gap-2">
                <DollarSign className="w-4 h-4 text-slate-400" /> Current Salary
              </span>
              <span className="font-bold text-emerald-600 text-base">
                {formatMoney(employee.salary)}
              </span>
            </div>

            <div className="pt-3 flex items-center justify-between">
              <span className="text-slate-500 text-xs flex items-center gap-2">
                <Calendar className="w-4 h-4 text-slate-400" /> Record Created
              </span>
              <span className="text-xs text-slate-600">
                {formatDateTime(employee.createdAt)}
              </span>
            </div>

            <div className="pt-3 flex items-center justify-between">
              <span className="text-slate-500 text-xs flex items-center gap-2">
                <Calendar className="w-4 h-4 text-slate-400" /> Last Updated
              </span>
              <span className="text-xs text-slate-600">
                {formatDateTime(employee.updatedAt)}
              </span>
            </div>
          </div>

          {/* Actions */}
          <div className="mt-6 pt-4 border-t border-slate-100 flex items-center justify-end gap-3">
            <button
              onClick={onClose}
              className="px-4 py-2 text-xs font-semibold text-slate-600 hover:text-slate-800 hover:bg-slate-100 rounded-lg transition-colors"
            >
              Close
            </button>
            <button
              onClick={() => {
                onClose();
                onEdit(employee);
              }}
              className="inline-flex items-center gap-1.5 px-4 py-2 text-xs font-semibold text-white bg-indigo-600 hover:bg-indigo-700 rounded-lg shadow-sm transition-colors"
            >
              <Edit2 className="w-3.5 h-3.5" />
              <span>Edit Employee</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
