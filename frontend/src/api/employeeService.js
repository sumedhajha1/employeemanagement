/**
 * API Service for Employee Management Backend
 * Interacts with Spring Boot endpoints at /api/employees
 */

const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/employees';

/**
 * Helper to handle HTTP responses and format backend errors
 */
async function handleResponse(response) {
  if (response.status === 204) {
    return null; // No Content
  }

  const contentType = response.headers.get('content-type');
  const isJson = contentType && contentType.includes('application/json');
  const data = isJson ? await response.json() : await response.text();

  if (!response.ok) {
    const error = new Error(
      (data && data.message) || response.statusText || 'Request failed'
    );
    error.status = response.status;
    error.details = data && data.details ? data.details : null;
    error.raw = data;
    throw error;
  }

  return data;
}

export const employeeService = {
  /**
   * Fetch paginated, sorted, and filtered employees
   */
  async getEmployees({ page = 0, size = 10, sortBy = 'id', direction = 'asc', search = '', department = '' }) {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
      sortBy,
      direction
    });

    if (search && search.trim() !== '') {
      params.append('search', search.trim());
    }

    if (department && department.trim() !== '') {
      params.append('department', department.trim());
    }

    const response = await fetch(`${BASE_URL}?${params.toString()}`, {
      headers: { 'Accept': 'application/json' }
    });
    return handleResponse(response);
  },

  /**
   * Fetch a single employee by ID
   */
  async getEmployeeById(id) {
    const response = await fetch(`${BASE_URL}/${id}`, {
      headers: { 'Accept': 'application/json' }
    });
    return handleResponse(response);
  },

  /**
   * Create a new employee
   */
  async createEmployee(payload) {
    const response = await fetch(BASE_URL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify(payload)
    });
    return handleResponse(response);
  },

  /**
   * Update an existing employee
   */
  async updateEmployee(id, payload) {
    const response = await fetch(`${BASE_URL}/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify(payload)
    });
    return handleResponse(response);
  },

  /**
   * Delete an employee by ID
   */
  async deleteEmployee(id) {
    const response = await fetch(`${BASE_URL}/${id}`, {
      method: 'DELETE',
      headers: { 'Accept': 'application/json' }
    });
    return handleResponse(response);
  },

  /**
   * Fetch aggregated statistics directly from the backend
   */
  async getStatistics() {
    const response = await fetch(`${BASE_URL}/statistics`, {
      headers: { 'Accept': 'application/json' }
    });
    return handleResponse(response);
  },

  /**
   * Fetch distinct active departments for the filter dropdown
   */
  async getDepartments() {
    const response = await fetch(`${BASE_URL}/departments`, {
      headers: { 'Accept': 'application/json' }
    });
    return handleResponse(response);
  }
};
