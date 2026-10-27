import React from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';

export default function Pagination({
  pageNumber,
  pageSize,
  totalElements,
  totalPages,
  onPageChange,
  onPageSizeChange
}) {
  if (totalElements === 0) return null;

  const startRecord = pageNumber * pageSize + 1;
  const endRecord = Math.min((pageNumber + 1) * pageSize, totalElements);

  // Generate page numbers to display
  const pages = [];
  const maxPagesToShow = 5;
  let startPage = Math.max(0, pageNumber - Math.floor(maxPagesToShow / 2));
  let endPage = Math.min(totalPages - 1, startPage + maxPagesToShow - 1);

  if (endPage - startPage + 1 < maxPagesToShow) {
    startPage = Math.max(0, endPage - maxPagesToShow + 1);
  }

  for (let i = startPage; i <= endPage; i++) {
    pages.push(i);
  }

  return (
    <div className="flex flex-col sm:flex-row items-center justify-between gap-4 py-4 px-6 border-t border-slate-200 bg-white rounded-b-xl text-sm">
      {/* Left: Summary and Page Size */}
      <div className="flex items-center gap-4 text-slate-500 text-xs sm:text-sm">
        <span>
          Showing <span className="font-semibold text-slate-800">{startRecord}</span> to{' '}
          <span className="font-semibold text-slate-800">{endRecord}</span> of{' '}
          <span className="font-semibold text-slate-800">{totalElements}</span> results
        </span>

        <div className="flex items-center gap-1.5">
          <label htmlFor="pageSize" className="text-xs text-slate-500">Per page:</label>
          <select
            id="pageSize"
            value={pageSize}
            onChange={(e) => onPageSizeChange(Number(e.target.value))}
            className="text-xs font-medium border border-slate-200 rounded px-2 py-1 bg-white text-slate-700 focus:outline-none focus:ring-1 focus:ring-indigo-500"
          >
            <option value={5}>5</option>
            <option value={10}>10</option>
            <option value={25}>25</option>
            <option value={50}>50</option>
          </select>
        </div>
      </div>

      {/* Right: Page Buttons */}
      <div className="flex items-center gap-1">
        {/* Previous Button */}
        <button
          onClick={() => onPageChange(pageNumber - 1)}
          disabled={pageNumber === 0}
          className="p-1.5 rounded-lg border border-slate-200 text-slate-600 hover:bg-slate-50 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
          title="Previous Page"
        >
          <ChevronLeft className="w-4 h-4" />
        </button>

        {/* First Page button if far */}
        {startPage > 0 && (
          <>
            <button
              onClick={() => onPageChange(0)}
              className="px-3 py-1 text-xs font-medium rounded-lg text-slate-600 hover:bg-slate-100"
            >
              1
            </button>
            {startPage > 1 && <span className="px-1 text-slate-400">...</span>}
          </>
        )}

        {/* Middle Page Numbers */}
        {pages.map((p) => (
          <button
            key={p}
            onClick={() => onPageChange(p)}
            className={`px-3 py-1 text-xs font-semibold rounded-lg transition-colors ${
              p === pageNumber
                ? 'bg-indigo-600 text-white shadow-sm'
                : 'text-slate-600 hover:bg-slate-100'
            }`}
          >
            {p + 1}
          </button>
        ))}

        {/* Last Page button if far */}
        {endPage < totalPages - 1 && (
          <>
            {endPage < totalPages - 2 && <span className="px-1 text-slate-400">...</span>}
            <button
              onClick={() => onPageChange(totalPages - 1)}
              className="px-3 py-1 text-xs font-medium rounded-lg text-slate-600 hover:bg-slate-100"
            >
              {totalPages}
            </button>
          </>
        )}

        {/* Next Button */}
        <button
          onClick={() => onPageChange(pageNumber + 1)}
          disabled={pageNumber >= totalPages - 1}
          className="p-1.5 rounded-lg border border-slate-200 text-slate-600 hover:bg-slate-50 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
          title="Next Page"
        >
          <ChevronRight className="w-4 h-4" />
        </button>
      </div>
    </div>
  );
}
