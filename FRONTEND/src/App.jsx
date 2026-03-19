import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from '@/context/AuthContext';
import Navbar from '@/components/Navbar';
import ProtectedRoute from '@/components/ProtectedRoute';
import HomePage from '@/pages/HomePage';
import LoginPage from '@/pages/LoginPage';
import RegisterPage from '@/pages/RegisterPage';
import DashboardPage from '@/pages/DashboardPage';
import AnalyticsPage from '@/pages/AnalyticsPage';

export default function App() {
  return (
    <Router>
      <AuthProvider>
        <div className="min-h-screen bg-background text-foreground">
          <Navbar />
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route
              path="/dashboard"
              element={
                <ProtectedRoute>
                  <DashboardPage />
                </ProtectedRoute>
              }
            />
            <Route
              path="/analytics/:shortCode"
              element={
                <ProtectedRoute>
                  <AnalyticsPage />
                </ProtectedRoute>
              }
            />
          </Routes>
        </div>
      </AuthProvider>
    </Router>
  );
}
