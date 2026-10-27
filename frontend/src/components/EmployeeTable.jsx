import React from 'react';
import {
  Search,
  Filter,
  ArrowUpDown,
  ArrowUp,
  ArrowDown,
  Edit2,
  Trash2,
  Eye,
  X,
  UserX,
  Building
} from 'lucide-react';

export default function EmployeeTable({
  employees,
  loading,
  sortBy,
  direction,
  onSort,
  search,
  onSearchChange,
  department,
  onDepartmentChange,
  departments,
  currency,
  onView,
  onEdit,
  onDelete,
  onAddClick
}) {
  const getSortIcon = (field) => {
    if (sortBy !== field) {
      return <ArrowUpDown className="w-3.5 h-3.5 text-slate-400 group-hover:text-slate-600" />;
    }
    return direction === 'asc' ? (
      <ArrowUp className="w-3.5 h-3.5 text-indigo-600" />
    ) : (
      <ArrowDown className="w-3.5 h-3.5 text-indigo-600" />
    );
  };

  const getDepartmentBadgeStyle = (dept) => {
    const map = {
      Engineering: 'bg-blue-50 text-blue-700 border-blue-200',
      'Human Resources': 'bg-pink-50 text-pink-700 border-pink-200',
      HR: 'bg-pink-50 text-pink-700 border-pink-200',
      Finance: 'bg-emerald-50 text-emerald-700 border-emerald-200',
      Marketing: 'bg-amber-50 text-amber-700 border-amber-200',
      Sales: 'bg-violet-50 text-violet-700 border-violet-200',
      Operations: 'bg-cyan-50 text-cyan-700 border-cyan-200',
      Management: 'bg-purple-50 text-purple-700 border-purple-200',
    };
    return map[dept] || 'bg-slate-100 text-slate-700 border-slate-200';
  };

  const formatSalary = (val) => {
    if (val === null || val === undefined) return `${currency.symbol}0.00`;
    return `${currency.symbol}${Number(val).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
  };

  const formatDate = (isoString) => {
    if (!isoString) return '-';
    try {
      const d = new Date(isoString);
      return d.toLocaleDateString(undefined, {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      });
    } catch {
      return isoString;
    }
  };

  const hasActiveFilters = Boolean(search || department);

  const clearFilters = () => {
    onSearchChange('');
    onDepartmentChange('');
  };

  return (
    <div className="bg-white rounded-xl shadow-sm border border-slate-200/80 overflow-hidden">
      {/* Table Toolbar */}
      <div className="p-4 sm:p-5 border-b border-slate-200 bg-slate-50/50 flex flex-col md:flex-row md:items-center justify-between gap-3">
        {/* Search Input */}
        <div className="relative flex-1 max-w-md">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            value={search}
            onChange={(e) => onSearchChange(e.target.value)}
            placeholder="Search by name or email..."
            className="w-full pl-9 pr-8 py-2 text-sm bg-white border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 transition-all text-slate-800 placeholder-slate-400"
          />
          {search && (
            <button
              onClick={() => onSearchChange('')}
              className="absolute right-2.5 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600"
            >
              <X className="w-4 h-4" />
            </button>
          )}
        </div>

        {/* Filter and Reset */}
        <div className="flex items-center gap-3">
          {/* Department Filter */}
          <div className="relative flex items-center">
            <Building className="w-3.5 h-3.5 text-slate-400 absolute left-3 pointer-events-none" />
            <select
              value={department}
              onChange={(e) => onDepartmentChange(e.target.value)}
              className="pl-8 pr-8 py-2 text-xs sm:text-sm font-medium bg-white border border-slate-200 rounded-lg text-slate-700 focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 cursor-pointer"
            >
              <option value="">All Departments</option>
              {departments.map((dept) => (
                <option key={dept} value={dept}>
                  {dept}
                </option>
              ))}
            </select>
          </div>

          {/* Reset Filters CTA */}
          {hasActiveFilters && (
            <button
              onClick={clearFilters}
              className="text-xs font-semibold text-rose-600 hover:text-rose-700 px-2 py-1 rounded bg-rose-50 border border-rose-200 transition-colors flex items-center gap-1"
            >
              <X className="w-3 h-3" /> Clear
            </button>
          )}
        </div>
      </div>

      {/* Main Table */}
      <div className="overflow-x-auto">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr className="bg-slate-50/70 border-b border-slate-200 text-xs font-semibold uppercase tracking-wider text-slate-500">
              <th
                onClick={() => onSort('id')}
                className="py-3 px-4 sm:px-6 cursor-pointer select-none group"
              >
                <div className="flex items-center gap-1.5">
                  <span>ID</span>
                  {getSortIcon('id')}
                </div>
              </th>
              <th
                onClick={() => onSort('name')}
                className="py-3 px-4 sm:px-6 cursor-pointer select-none group"
              >
                <div className="flex items-center gap-1.5">
                  <span>Employee</span>
                  {getSortIcon('name')}
                </div>
              </th>
              <th
                onClick={() => onSort('department')}
                className="py-3 px-4 sm:px-6 cursor-pointer select-none group"
              >
                <div className="flex items-center gap-1.5">
                  <span>Department</span>
                  {getSortIcon('department')}
                </div>
              </th>
              <th
                onClick={() => onSort('salary')}
                className="py-3 px-4 sm:px-6 cursor-pointer select-none group"
              >
                <div className="flex items-center gap-1.5">
                  <span>Salary</span>
                  {getSortIcon('salary')}
                </div>
              </th>
              <th
                onClick={() => onSort('createdAt')}
                className="py-3 px-4 sm:px-6 cursor-pointer select-none group hidden md:table-cell"
              >
                <div className="flex items-center gap-1.5">
                  <span>Joined Date</span>
                  {getSortIcon('createdAt')}
                </div>
              </th>
              <th className="py-3 px-4 sm:px-6 text-right">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100 text-sm">
            {loading ? (
              // Loading Skeleton
              Array.from({ length: 5 }).map((_, idx) => (
                <tr key={idx} className="animate-pulse">
                  <td className="py-4 px-6"><div className="h-4 w-6 bg-slate-200 rounded"></div></td>
                  <td className="py-4 px-6">
                    <div className="h-4 w-32 bg-slate-200 rounded mb-1.5"></div>
                    <div className="h-3 w-40 bg-slate-100 rounded"></div>
                  </td>
                  <td className="py-4 px-6"><div className="h-5 w-24 bg-slate-200 rounded-full"></div></td>
                  <td className="py-4 px-6"><div className="h-4 w-20 bg-slate-200 rounded"></div></td>
                  <td className="py-4 px-6 hidden md:table-cell"><div className="h-4 w-20 bg-slate-200 rounded"></div></td>
                  <td className="py-4 px-6 text-right"><div className="h-6 w-16 bg-slate-200 rounded ml-auto"></div></td>
                </tr>
              ))
            ) : employees.length === 0 ? (
              // Empty State
              <tr>
                <td colSpan={6} className="py-12 px-6 text-center">
                  <div className="max-w-sm mx-auto flex flex-col items-center">
                    <div className="w-12 h-12 rounded-full bg-slate-100 flex items-center justify-center text-slate-400 mb-3">
                      <UserX className="w-6 h-6" />
                    </div>
                    <h3 className="text-base font-semibold text-slate-800">
                      No employees found
                    </h3>
                    <p className="text-xs text-slate-500 mt-1 mb-4">
                      {hasActiveFilters
                        ? 'Try adjusting your search query or department filter.'
                        : 'Get started by adding your first employee to the database.'}
                    </p>
                    {hasActiveFilters ? (
                      <button
                        onClick={clearFilters}
                        className="px-3.5 py-1.5 text-xs font-semibold text-indigo-600 bg-indigo-50 hover:bg-indigo-100 rounded-lg border border-indigo-200 transition-colors"
                      >
                        Clear Active Filters
                      </button>
                    ) : (
                      <button
                        onClick={onAddClick}
                        className="px-4 py-2 text-xs font-semibold text-white bg-indigo-600 hover:bg-indigo-700 rounded-lg shadow-sm transition-colors"
                      >
                        Add New Employee
                      </button>
                    )}
                  </div>
                </td>
              </tr>
            ) : (
              // Real Data Rows
              employees.map((emp) => {
                const initials = emp.name
                  ? emp.name
                      .split(' ')
                      .map((n) => n[0])
                      .slice(0, 2)
                      .join('')
                      .toUpperCase()
                  : 'EM';

                return (
                  <tr
                    key={emp.id}
                    className="hover:bg-slate-50/80 transition-colors group"
                  >
                    <td className="py-3.5 px-4 sm:px-6 font-mono text-xs text-slate-400">
                      #{emp.id}
                    </td>
                    <td className="py-3.5 px-4 sm:px-6">
                      <div className="flex items-center gap-3">
                        <div className="w-9 h-9 rounded-full bg-indigo-100 text-indigo-700 flex items-center justify-center font-bold text-xs shrink-0">
                          {initials}
                        </div>
                        <div>
                          <div className="font-semibold text-slate-900 group-hover:text-indigo-600 transition-colors">
                            {emp.name}
                          </div>
                          <div className="text-xs text-slate-500">
                            {emp.email}
                          </div>
                        </div>
                      </div>
                    </td>
                    <td className="py-3.5 px-4 sm:px-6">
                      <span
                        className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold border ${getDepartmentBadgeStyle(
                          emp.department
                        )}`}
                      >
                        {emp.department}
                      </span>
                    </td>
                    <td className="py-3.5 px-4 sm:px-6 font-semibold text-slate-900">
                      {formatSalary(emp.salary)}
                    </td>
                    <td className="py-3.5 px-4 sm:px-6 text-xs text-slate-500 hidden md:table-cell">
                      {formatDate(emp.createdAt)}
                    </td>
                    <td className="py-3.5 px-4 sm:px-6 text-right">
                      <div className="flex items-center justify-end gap-1">
                        <button
                          onClick={() => onView(emp)}
                          className="p-1.5 text-slate-400 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition-colors"
                          title="View Employee Profile"
                        >
                          <Eye className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => onEdit(emp)}
                          className="p-1.5 text-slate-400 hover:text-amber-600 hover:bg-amber-50 rounded-lg transition-colors"
                          title="Edit Details"
                        >
                          <Edit2 className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => onDelete(emp)}
                          className="p-1.5 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors"
                          title="Delete Employee"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
