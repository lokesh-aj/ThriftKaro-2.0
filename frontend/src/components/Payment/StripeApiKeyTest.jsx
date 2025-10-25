import React, { useState, useEffect } from 'react';
import { paymentService } from '../../api/paymentService';
import { toast } from 'react-toastify';

const StripeApiKeyTest = () => {
  const [stripeApiKey, setStripeApiKey] = useState(null);
  const [loading, setLoading] = useState(false);
  const [healthStatus, setHealthStatus] = useState(null);

  const fetchStripeApiKey = async () => {
    setLoading(true);
    try {
      const response = await paymentService.getStripeApiKey();
      setStripeApiKey(response.stripeApiKey);
      toast.success('Stripe API key fetched successfully!');
    } catch (error) {
      console.error('Error fetching Stripe API key:', error);
      toast.error('Failed to fetch Stripe API key');
    } finally {
      setLoading(false);
    }
  };

  const checkHealth = async () => {
    try {
      const response = await paymentService.checkHealth();
      setHealthStatus(response);
      toast.success('Payment service is healthy!');
    } catch (error) {
      console.error('Error checking health:', error);
      toast.error('Payment service health check failed');
    }
  };

  useEffect(() => {
    // Auto-fetch on component mount
    fetchStripeApiKey();
    checkHealth();
  }, []);

  return (
    <div className="w-full max-w-2xl mx-auto p-6 bg-white rounded-lg shadow-md">
      <h2 className="text-2xl font-bold mb-6 text-center">Stripe API Key Test</h2>
      
      {/* Health Status */}
      <div className="mb-6 p-4 bg-gray-50 rounded-lg">
        <h3 className="text-lg font-semibold mb-2">Payment Service Health</h3>
        <div className="flex items-center gap-2">
          <button
            onClick={checkHealth}
            className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
          >
            Check Health
          </button>
          {healthStatus && (
            <span className="text-green-600 font-medium">✓ {healthStatus}</span>
          )}
        </div>
      </div>

      {/* Stripe API Key */}
      <div className="mb-6 p-4 bg-gray-50 rounded-lg">
        <h3 className="text-lg font-semibold mb-2">Stripe API Key</h3>
        <div className="flex items-center gap-2 mb-3">
          <button
            onClick={fetchStripeApiKey}
            disabled={loading}
            className="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600 disabled:bg-gray-400"
          >
            {loading ? 'Loading...' : 'Fetch API Key'}
          </button>
        </div>
        
        {stripeApiKey && (
          <div className="mt-3">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Public Key:
            </label>
            <div className="p-3 bg-gray-100 rounded border font-mono text-sm break-all">
              {stripeApiKey}
            </div>
            <div className="mt-2 text-sm text-gray-600">
              <strong>Status:</strong> 
              <span className={`ml-1 ${stripeApiKey.startsWith('pk_') ? 'text-green-600' : 'text-red-600'}`}>
                {stripeApiKey.startsWith('pk_') ? '✓ Valid Stripe Public Key' : '⚠ Invalid Key Format'}
              </span>
            </div>
          </div>
        )}
      </div>

      {/* Integration Status */}
      <div className="p-4 bg-blue-50 rounded-lg">
        <h3 className="text-lg font-semibold mb-2">Integration Status</h3>
        <div className="space-y-2 text-sm">
          <div className="flex items-center gap-2">
            <span className={`w-3 h-3 rounded-full ${healthStatus ? 'bg-green-500' : 'bg-gray-300'}`}></span>
            <span>Payment Service Connection</span>
          </div>
          <div className="flex items-center gap-2">
            <span className={`w-3 h-3 rounded-full ${stripeApiKey ? 'bg-green-500' : 'bg-gray-300'}`}></span>
            <span>Stripe API Key Available</span>
          </div>
          <div className="flex items-center gap-2">
            <span className={`w-3 h-3 rounded-full ${stripeApiKey?.startsWith('pk_') ? 'bg-green-500' : 'bg-gray-300'}`}></span>
            <span>Valid Key Format</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default StripeApiKeyTest;







