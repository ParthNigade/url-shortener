import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';
import { Button } from '@/components/ui/button';
import { Link2, LogOut, LayoutDashboard } from 'lucide-react';

export default function Navbar() {
  const { user, logout, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="sticky top-0 z-50 border-b border-border/50 bg-background/80 backdrop-blur-xl">
      <div className="mx-auto max-w-6xl flex items-center justify-between px-6 h-16">
        <Link to="/" className="flex items-center gap-2 text-lg font-bold tracking-tight">
          <div className="flex items-center justify-center w-8 h-8 rounded-lg bg-primary/15">
            <Link2 className="w-4 h-4 text-primary" />
          </div>
          <span className="bg-gradient-to-r from-primary to-purple-400 bg-clip-text text-transparent">
            SnapLink
          </span>
        </Link>

        <div className="flex items-center gap-3">
          {isAuthenticated ? (
            <>
              <Link to="/dashboard">
                <Button variant="ghost" size="sm" className="gap-2">
                  <LayoutDashboard className="w-4 h-4" />
                  Dashboard
                </Button>
              </Link>
              <span className="text-sm text-muted-foreground">
                Hi, <span className="text-foreground font-medium">{user?.username}</span>
              </span>
              <Button variant="outline" size="sm" onClick={handleLogout} className="gap-2">
                <LogOut className="w-4 h-4" />
                Logout
              </Button>
            </>
          ) : (
            <>
              <Link to="/login">
                <Button variant="ghost" size="sm">Login</Button>
              </Link>
              <Link to="/register">
                <Button size="sm">Sign Up</Button>
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}
