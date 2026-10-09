import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { ClipboardList, Clock, RefreshCw, ChevronRight, Compass } from 'lucide-react';
import { api } from '../services/api';

export default function MyOrdersPage() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const navigate = useNavigate();

  const fetchOrders = async (showRefreshIndicator = false) => {
    if (showRefreshIndicator) setRefreshing(true);
    else setLoading(true);

    try {
      const savedOrderIds = JSON.parse(localStorage.getItem('placedOrders') || '[]');
      if (savedOrderIds.length === 0) {
        setOrders([]);
        setLoading(false);
        setRefreshing(false);
        return;
      }

      // Fetch each order's details from the API
      const fetched = await Promise.all(
        savedOrderIds.map(async (id) => {
          try {
            const details = await api.trackOrder(id);
            return { success: true, data: details };
          } catch (err) {
            console.warn(`Failed to fetch status for order #${id}`, err);
            return { success: false, id };
          }
        })
      );

      // Filter to successful fetches or keep basic details
      const validOrders = fetched
        .map((res, index) => {
          if (res.success) return res.data;
          // Return placeholder if endpoint fails (e.g. order deleted from db)
          const id = savedOrderIds[index];
          return {
            id,
            tableNumber: '?',
            status: 'ARCHIVED',
            totalPrice: 0.0,
            paymentMethod: '?',
            createdAt: new Date().toISOString()
          };
        })
        // Sort newest first
        .sort((a, b) => b.id - a.id);

      setOrders(validOrders);
    } catch (err) {
      console.error('Error fetching customer orders:', err);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  const getStatusColor = (status) => {
    switch (status) {
      case 'RECEIVED':
        return 'bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-300';
      case 'PREPARING':
        return 'bg-amber-100 text-amber-800 dark:bg-amber-900/30 dark:text-amber-300';
      case 'READY':
        return 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-300';
      case 'SERVED':
        return 'bg-gray-100 text-gray-800 dark:bg-gray-900/30 dark:text-gray-400';
      case 'CANCELLED':
        return 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-300';
      default:
        return 'bg-gray-100 text-gray-600 dark:bg-gray-800 dark:text-gray-400';
    }
  };

  const formatDate = (isoString) => {
    try {
      const date = new Date(isoString);
      return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) + ' - ' + date.toLocaleDateString([], { month: 'short', day: 'numeric' });
    } catch (e) {
      return 'Recent';
    }
  };

  return (
    <div className="max-w-3xl mx-auto px-4 py-8 min-h-[70vh]">
      {/* Header */}
      <div className="flex justify-between items-center mb-8">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 bg-cafe-wood text-white dark:bg-cafe-gold dark:text-cafe-charcoal rounded-xl flex items-center justify-center shadow-md">
            <ClipboardList className="w-5 h-5" />
          </div>
          <div>
            <h1 className="font-serif text-2xl font-bold dark:text-white leading-tight">My Orders</h1>
            <p className="text-xs text-gray-500 dark:text-gray-400 font-light">Track your active or past orders at this table</p>
          </div>
        </div>

        {orders.length > 0 && (
          <button
            onClick={() => fetchOrders(true)}
            disabled={refreshing}
            className="p-2.5 rounded-xl border border-cafe-gold/20 text-cafe-wood dark:text-cafe-gold hover:bg-cafe-wood/5 transition flex items-center gap-1.5 text-xs font-semibold"
          >
            <RefreshCw className={`w-3.5 h-3.5 ${refreshing ? 'animate-spin' : ''}`} />
            <span>Refresh</span>
          </button>
        )}
      </div>

      {loading ? (
        <div className="flex flex-col justify-center items-center py-20">
          <div className="w-10 h-10 border-4 border-cafe-wood dark:border-cafe-gold border-t-transparent rounded-full animate-spin mb-4" />
          <p className="text-sm text-gray-400 font-light">Retrieving order list...</p>
        </div>
      ) : orders.length === 0 ? (
        <div className="bg-white dark:bg-cafe-chocolate/10 border border-cafe-gold/15 rounded-2xl p-10 text-center shadow-sm flex flex-col items-center justify-center">
          <ClipboardList className="w-16 h-16 text-gray-250 dark:text-cafe-wood/25 mb-4 animate-bounce-subtle" />
          <h3 className="font-serif text-lg font-bold dark:text-white mb-2">No Orders Placed Yet</h3>
          <p className="text-sm text-gray-400 dark:text-gray-500 font-light max-w-sm mb-6 leading-relaxed">
            You haven't placed any orders from this device yet. Visit our interactive menu to select your tropical treats!
          </p>
          <Link
            to="/"
            className="px-6 py-3 bg-cafe-wood text-white dark:bg-cafe-gold dark:text-cafe-charcoal rounded-xl font-bold hover:scale-103 hover:shadow-md transition flex items-center gap-2"
          >
            <Compass className="w-4 h-4" />
            <span>Browse Menu</span>
          </Link>
        </div>
      ) : (
        <div className="space-y-4">
          {orders.map((order) => (
            <div
              key={order.id}
              onClick={() => order.status !== 'ARCHIVED' && navigate(`/track?orderId=${order.id}`)}
              className={`bg-white dark:bg-cafe-charcoal border border-cafe-gold/15 rounded-2xl p-5 shadow-sm transition hover:shadow-md ${
                order.status !== 'ARCHIVED' ? 'cursor-pointer hover:border-cafe-gold/30' : 'opacity-60 cursor-not-allowed'
              }`}
            >
              <div className="flex justify-between items-start gap-4 mb-3">
                <div>
                  <div className="flex items-center gap-2">
                    <span className="font-bold text-sm text-gray-800 dark:text-white">Order #{order.id}</span>
                    <span className="text-[10px] px-2 py-0.5 bg-gray-50 dark:bg-cafe-chocolate/30 text-gray-400 dark:text-gray-400 rounded-full font-medium">
                      Table {order.tableNumber}
                    </span>
                  </div>
                  <span className="text-[10px] text-gray-400 dark:text-gray-500 font-light block mt-0.5">
                    {formatDate(order.createdAt)}
                  </span>
                </div>

                <span className={`text-[10px] font-extrabold uppercase px-2.5 py-1 rounded-md tracking-wider ${getStatusColor(order.status)}`}>
                  {order.status}
                </span>
              </div>

              {/* Items Summary */}
              {order.orderItems && order.orderItems.length > 0 && (
                <div className="text-xs font-light text-gray-500 dark:text-gray-400 border-t border-b border-gray-100 dark:border-cafe-wood/10 py-3 my-3">
                  {order.orderItems.map((item, idx) => (
                    <span key={item.id} className="inline-block mr-3 mb-1">
                      <strong className="font-semibold text-gray-700 dark:text-gray-200">x{item.quantity}</strong> {item.menuItem?.name}
                      {idx < order.orderItems.length - 1 && ','}
                    </span>
                  ))}
                </div>
              )}

              <div className="flex justify-between items-center mt-2.5">
                <div className="text-xs text-gray-400 dark:text-gray-500">
                  Total Paid: <strong className="text-sm font-bold text-cafe-wood dark:text-cafe-gold ml-1">₹{order.totalPrice?.toFixed(2)}</strong>
                </div>

                {order.status !== 'ARCHIVED' && (
                  <span className="text-xs text-cafe-wood dark:text-cafe-gold font-bold flex items-center gap-1">
                    <span>Track Live</span>
                    <ChevronRight className="w-3.5 h-3.5" />
                  </span>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
