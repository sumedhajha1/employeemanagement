import React, { useState, useEffect, useCallback } from 'react';
import Header from './components/Header';
import Sidebar from './components/Sidebar';
import StatsOverview from './components/StatsOverview';
import EmployeeTable from './components/EmployeeTable';
import Pagination from './components/Pagination';
import EmployeeModal from './components/EmployeeModal';
import EmployeeDetailsModal from './components/EmployeeDetailsModal';
import DeleteConfirmModal from './components/DeleteConfirmModal';
import Toast from './components/Toast';
import { employeeService } from './api/employeeService';
import { AlertCircle, RefreshCw } from 'lucide-react';

const CURRENCIES = [
  { code: 'USD', symbol: '$' },
  { code: 'INR', symbol: '₹' },
  { code: 'EUR', symbol: '€' },
  { code: 'GBP', symbol: '£' },
];

export default function App() {
  // Navigation
  const [activeTab, setActiveTab] = useState('dashboard');

  // Currency configuration
  const [currency, setCurrency] = useState(CURRENCIES[0]);

  // Data states
  const [employees, setEmployees] = useState([]);
  const [statistics, setStatistics] = useState(null);
  const [departments, setDepartments] = useState([]);

  // Loading & error states
  const [loading, setLoading] = useState(true);
  const [statsLoading, setStatsLoading] = useState(true);
  const [apiError, setApiError] = useState(null);

  // Pagination & filter parameters
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [sortBy, setSortBy] = useState('id');
  const [direction, setDirection] = useState('asc');
  const [search, setSearch] = useState('');
  const [selectedDept, setSelectedDept] = useState('');
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  // Modals state
  const [employeeModal, setEmployeeModal] = useState({ isOpen: false, employee: null });
  const [detailsModal, setDetailsModal] = useState({ isOpen: false, employee: null });
  const [deleteModal, setDeleteModal] = useState({ isOpen: false, employee: null, deleting: false });

  // Notifications
  const [toast, setToast] = useState(null);

  const showToast = (type, message) => {
    setToast({ type, message });
  };

  /**
   * Fetch live statistics from backend
   */
  const loadStatistics = useCallback(async () => {
    setStatsLoading(true);
    try {
      const stats = await employeeService.getStatistics();
      setStatistics(stats);
    } catch (err) {
      console.error('Failed to load statistics:', err);
    } finally {
      setStatsLoading(false);
    }
  }, []);

  /**
   * Fetch distinct departments from backend
   */
  const loadDepartments = useCallback(async () => {
    try {
      const depts = await employeeService.getDepartments();
      setDepartments(depts || []);
    } catch (err) {
      console.error('Failed to load departments:', err);
    }
  }, []);

  /**
   * Fetch paginated & filtered employees from backend
   */
  const loadEmployees = useCallback(async () => {
    setLoading(true);
    setApiError(null);
    try {
      const data = await employeeService.getEmployees({
        page,
        size,
        sortBy,
        direction,
        search,
        department: selectedDept
      });

      setEmployees(data.content || []);
      setTotalPages(data.totalPages || 0);
      setTotalElements(data.totalElements || 0);
    } catch (err) {
      console.error('Failed to load employees:', err);
      setApiError(err.message || 'Could not connect to the Employee REST API at http://localhost:8080');
      setEmployees([]);
    } finally {
      setLoading(false);
    }
  }, [page, size, sortBy, direction, search, selectedDept]);

  // Initial load
  useEffect(() => {
    loadStatistics();
    loadDepartments();
  }, [loadStatistics, loadDepartments]);

  // Load employees when pagination/sorting/filters change
  useEffect(() => {
    loadEmployees();
  }, [loadEmployees]);

  // Reset page when filters change
  const handleSearchChange = (query) => {
    setSearch(query);
    setPage(0);
  };

  const handleDepartmentChange = (dept) => {
    setSelectedDept(dept);
    setPage(0);
  };

  const handleSort = (field) => {
    if (sortBy === field) {
      setDirection(direction === 'asc' ? 'desc' : 'asc');
    } else {
      setSortBy(field);
      setDirection('asc');
    }
    setPage(0);
  };

  const handleRefreshAll = () => {
    loadStatistics();
    loadDepartments();
    loadEmployees();
    showToast('success', 'Data refreshed successfully');
  };

  // Create or Update handler
  const handleSaveEmployee = async (payload) => {
    if (employeeModal.employee) {
      // Update
      const updated = await employeeService.updateEmployee(employeeModal.employee.id, payload);
      showToast('success', `Employee #${updated.id} (${updated.name}) updated successfully`);
    } else {
      // Create
      const created = await employeeService.createEmployee(payload);
      showToast('success', `Employee #${created.id} (${created.name}) created successfully`);
    }
    // Refresh all data
    loadEmployees();
    loadStatistics();
    loadDepartments();
  };

  // Delete handler
  const handleDeleteConfirm = async () => {
    if (!deleteModal.employee) return;
    setDeleteModal((prev) => ({ ...prev, deleting: true }));

    try {
      await employeeService.deleteEmployee(deleteModal.employee.id);
      showToast('success', `Employee #${deleteModal.employee.id} deleted successfully`);
      setDeleteModal({ isOpen: false, employee: null, deleting: false });

      // Refresh data
      loadEmployees();
      loadStatistics();
      loadDepartments();
    } catch (err) {
      showToast('error', err.message || 'Failed to delete employee');
      setDeleteModal((prev) => ({ ...prev, deleting: false }));
    }
  };

  return (
    <div className="flex h-screen bg-slate-100/60 overflow-hidden font-sans">
      {/* Sidebar Navigation */}
      <Sidebar activeTab={activeTab} setActiveTab={setActiveTab} />

      {/* Main Content Area */}
      <div className="flex-1 flex flex-col h-full overflow-y-auto">
        {/* Top Header */}
        <Header
          onAddClick={() => setEmployeeModal({ isOpen: true, employee: null })}
          onRefresh={handleRefreshAll}
          loading={loading || statsLoading}
          currency={currency}
          onCurrencyChange={setCurrency}
          availableCurrencies={CURRENCIES}
        />

        {/* Dashboard Body */}
        <main className="flex-1 p-4 sm:p-6 lg:p-8 max-w-7xl w-full mx-auto">
          {/* Backend Connection Error Banner */}
          {apiError && (
            <div className="mb-6 p-4 rounded-xl bg-rose-50 border border-rose-200 text-rose-800 flex items-start gap-3 shadow-sm">
              <AlertCircle className="w-5 h-5 text-rose-600 shrink-0 mt-0.5" />
              <div className="flex-1 text-xs sm:text-sm">
                <p className="font-semibold text-rose-900">API Connection Notice</p>
                <p className="mt-0.5 text-rose-700 leading-relaxed">
                  {apiError}. Make sure the Spring Boot server is running on{' '}
                  <code className="bg-rose-100 px-1.5 py-0.5 rounded font-mono text-xs">
                    http://localhost:8080
                  </code>
                </p>
              </div>
              <button
                onClick={loadEmployees}
                className="inline-flex items-center gap-1.5 px-3 py-1.5 text-xs font-semibold text-rose-700 bg-white hover:bg-rose-100 border border-rose-200 rounded-lg transition-colors"
              >
                <RefreshCw className="w-3.5 h-3.5" /> Retry
              </button>
            </div>
          )}

          {/* Overview Metric Cards */}
          <StatsOverview
            statistics={statistics}
            loading={statsLoading}
            currency={currency}
          />

          {/* Employee Directory Section Header */}
          <div className="mb-4 flex items-center justify-between">
            <div>
              <h2 className="text-lg font-bold text-slate-900">
                Staff Records
              </h2>
              <p className="text-xs text-slate-500">
                Live records loaded from PostgreSQL database
              </p>
            </div>
          </div>

          {/* Employee Table */}
          <EmployeeTable
            employees={employees}
            loading={loading}
            sortBy={sortBy}
            direction={direction}
            onSort={handleSort}
            search={search}
            onSearchChange={handleSearchChange}
            department={selectedDept}
            onDepartmentChange={handleDepartmentChange}
            departments={departments}
            currency={currency}
            onView={(emp) => setDetailsModal({ isOpen: true, employee: emp })}
            onEdit={(emp) => setEmployeeModal({ isOpen: true, employee: emp })}
            onDelete={(emp) => setDeleteModal({ isOpen: true, employee: emp, deleting: false })}
            onAddClick={() => setEmployeeModal({ isOpen: true, employee: null })}
          />

          {/* Pagination Controls */}
          <Pagination
            pageNumber={page}
            pageSize={size}
            totalElements={totalElements}
            totalPages={totalPages}
            onPageChange={setPage}
            onPageSizeChange={(newSize) => {
              setSize(newSize);
              setPage(0);
            }}
          />
        </main>
      </div>

      {/* Add / Edit Employee Modal */}
      <EmployeeModal
        isOpen={employeeModal.isOpen}
        employee={employeeModal.employee}
        onClose={() => setEmployeeModal({ isOpen: false, employee: null })}
        onSubmit={handleSaveEmployee}
        departments={departments}
        currency={currency}
      />

      {/* View Details Modal */}
      <EmployeeDetailsModal
        isOpen={detailsModal.isOpen}
        employee={detailsModal.employee}
        onClose={() => setDetailsModal({ isOpen: false, employee: null })}
        onEdit={(emp) => setEmployeeModal({ isOpen: true, employee: emp })}
        currency={currency}
      />

      {/* Delete Confirmation Modal */}
      <DeleteConfirmModal
        isOpen={deleteModal.isOpen}
        employee={deleteModal.employee}
        deleting={deleteModal.deleting}
        onClose={() => setDeleteModal({ isOpen: false, employee: null, deleting: false })}
        onConfirm={handleDeleteConfirm}
      />

      {/* Floating Notifications */}
      <Toast toast={toast} onClose={() => setToast(null)} />
    </div>
  );
}
