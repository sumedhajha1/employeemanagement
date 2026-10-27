import React from 'react';
import { Users, Building2, TrendingUp, Award } from 'lucide-react';

export default function StatsOverview({ statistics, loading, currency }) {
  const formatMoney = (amount) => {
    if (amount === null || amount === undefined) return `${currency.symbol}0.00`;
    const num = Number(amount);
    return `${currency.symbol}${num.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
  };

  const cards = [
    {
      title: 'Total Employees',
      value: loading ? '-' : (statistics?.totalEmployees ?? 0).toLocaleString(),
      subtitle: 'Active staff members',
      icon: Users,
      color: 'from-blue-500 to-indigo-600',
      textColor: 'text-indigo-600',
      bgColor: 'bg-indigo-50',
      borderColor: 'border-indigo-100'
    },
    {
      title: 'Total Departments',
      value: loading ? '-' : (statistics?.totalDepartments ?? 0).toLocaleString(),
      subtitle: 'Distinct business units',
      icon: Building2,
      color: 'from-purple-500 to-violet-600',
      textColor: 'text-purple-600',
      bgColor: 'bg-purple-50',
      borderColor: 'border-purple-100'
    },
    {
      title: 'Average Salary',
      value: loading ? '-' : formatMoney(statistics?.averageSalary),
      subtitle: 'Mean compensation',
      icon: TrendingUp,
      color: 'from-emerald-500 to-teal-600',
      textColor: 'text-emerald-600',
      bgColor: 'bg-emerald-50',
      borderColor: 'border-emerald-100'
    },
    {
      title: 'Highest Salary',
      value: loading ? '-' : formatMoney(statistics?.highestSalary),
      subtitle: 'Peak compensation',
      icon: Award,
      color: 'from-amber-500 to-orange-600',
      textColor: 'text-amber-600',
      bgColor: 'bg-amber-50',
      borderColor: 'border-amber-100'
    }
  ];

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 sm:gap-6 mb-8">
      {cards.map((card, index) => {
        const IconComponent = card.icon;
        return (
          <div
            key={index}
            className="bg-white rounded-xl p-5 shadow-sm border border-slate-200/80 transition-all duration-200 hover:shadow-md hover:border-slate-300"
          >
            <div className="flex items-center justify-between">
              <div>
                <p className="text-xs font-semibold uppercase tracking-wider text-slate-500">
                  {card.title}
                </p>
                <div className="mt-2 flex items-baseline gap-2">
                  {loading ? (
                    <div className="h-8 w-24 bg-slate-200 animate-pulse rounded"></div>
                  ) : (
                    <span className="text-2xl font-bold tracking-tight text-slate-900">
                      {card.value}
                    </span>
                  )}
                </div>
                <p className="mt-1 text-xs text-slate-400 font-medium">
                  {card.subtitle}
                </p>
              </div>
              <div className={`p-3 rounded-xl ${card.bgColor} ${card.borderColor} border`}>
                <IconComponent className={`w-6 h-6 ${card.textColor}`} />
              </div>
            </div>
          </div>
        );
      })}
    </div>
  );
}
