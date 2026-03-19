import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import api from '@/lib/api';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { ArrowLeft, BarChart3, MousePointerClick, Globe, Clock, Monitor, Loader2 } from 'lucide-react';

export default function AnalyticsPage() {
  const { shortCode } = useParams();
  const [stats, setStats] = useState(null);
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
  }, [shortCode]);

  const fetchData = async () => {
    try {
      const [statsRes, analyticsRes] = await Promise.all([
        api.get(`/url/${shortCode}/stats`),
        api.get(`/v1/analytics/${shortCode}`),
      ]);
      setStats(statsRes.data);
      setAnalytics(analyticsRes.data);
    } catch (err) {
      console.error('Failed to fetch analytics:', err);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return '—';
    return new Date(dateStr).toLocaleString('en-IN', {
      day: 'numeric', month: 'short', year: 'numeric',
      hour: '2-digit', minute: '2-digit'
    });
  };

  if (loading) {
    return (
      <div className="min-h-[calc(100vh-4rem)] flex items-center justify-center">
        <Loader2 className="w-8 h-8 animate-spin text-primary" />
      </div>
    );
  }

  if (!stats) {
    return (
      <div className="mx-auto max-w-4xl px-6 py-10 text-center">
        <p className="text-muted-foreground">URL not found</p>
        <Link to="/dashboard"><Button variant="outline" className="mt-4">Back to Dashboard</Button></Link>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-4xl px-6 py-10">
      {/* Back button */}
      <Link to="/dashboard" className="inline-flex items-center gap-1.5 text-sm text-muted-foreground hover:text-foreground mb-6 transition-colors">
        <ArrowLeft className="w-4 h-4" />
        Back to Dashboard
      </Link>

      {/* Header */}
      <div className="mb-8">
        <div className="flex items-center gap-3 mb-2">
          <h1 className="text-2xl font-bold tracking-tight">/{shortCode}</h1>
          <Badge variant={stats.isActive ? "success" : "destructive"}>
            {stats.isActive ? 'Active' : 'Inactive'}
          </Badge>
        </div>
        <p className="text-sm text-muted-foreground truncate">{stats.originalUrl}</p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
        <Card className="border-border/50">
          <CardContent className="p-5">
            <div className="flex items-center gap-3">
              <div className="flex items-center justify-center w-10 h-10 rounded-lg bg-primary/10">
                <MousePointerClick className="w-5 h-5 text-primary" />
              </div>
              <div>
                <p className="text-2xl font-bold">{stats.totalClicks}</p>
                <p className="text-xs text-muted-foreground">Total Clicks</p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card className="border-border/50">
          <CardContent className="p-5">
            <div className="flex items-center gap-3">
              <div className="flex items-center justify-center w-10 h-10 rounded-lg bg-emerald-500/10">
                <Clock className="w-5 h-5 text-emerald-400" />
              </div>
              <div>
                <p className="text-sm font-medium">{formatDate(stats.createdAt)}</p>
                <p className="text-xs text-muted-foreground">Created</p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card className="border-border/50">
          <CardContent className="p-5">
            <div className="flex items-center gap-3">
              <div className="flex items-center justify-center w-10 h-10 rounded-lg bg-amber-500/10">
                <Clock className="w-5 h-5 text-amber-400" />
              </div>
              <div>
                <p className="text-sm font-medium">{formatDate(stats.expiresAt)}</p>
                <p className="text-xs text-muted-foreground">Expires</p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Recent Clicks Table */}
      <Card className="border-border/50">
        <CardHeader>
          <CardTitle className="flex items-center gap-2 text-lg">
            <BarChart3 className="w-5 h-5 text-primary" />
            Recent Clicks
          </CardTitle>
        </CardHeader>
        <CardContent>
          {analytics?.recentClicks?.length > 0 ? (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-border/50">
                    <th className="text-left py-3 px-2 text-xs font-medium text-muted-foreground uppercase tracking-wider">Time</th>
                    <th className="text-left py-3 px-2 text-xs font-medium text-muted-foreground uppercase tracking-wider">IP Address</th>
                    <th className="text-left py-3 px-2 text-xs font-medium text-muted-foreground uppercase tracking-wider">Browser / Agent</th>
                    <th className="text-left py-3 px-2 text-xs font-medium text-muted-foreground uppercase tracking-wider">Referer</th>
                  </tr>
                </thead>
                <tbody>
                  {analytics.recentClicks.map((click, i) => (
                    <tr key={i} className="border-b border-border/30 hover:bg-muted/30 transition-colors">
                      <td className="py-3 px-2 whitespace-nowrap">
                        <div className="flex items-center gap-1.5">
                          <Clock className="w-3 h-3 text-muted-foreground" />
                          {formatDate(click.clickedAt)}
                        </div>
                      </td>
                      <td className="py-3 px-2">
                        <div className="flex items-center gap-1.5">
                          <Globe className="w-3 h-3 text-muted-foreground" />
                          <code className="text-xs bg-muted px-1.5 py-0.5 rounded">{click.ipAddress || '—'}</code>
                        </div>
                      </td>
                      <td className="py-3 px-2 max-w-[200px]">
                        <div className="flex items-center gap-1.5">
                          <Monitor className="w-3 h-3 text-muted-foreground shrink-0" />
                          <span className="truncate text-xs">{click.userAgent || '—'}</span>
                        </div>
                      </td>
                      <td className="py-3 px-2 text-xs text-muted-foreground">{click.referer || '(direct)'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <div className="text-center py-10 text-muted-foreground">
              <MousePointerClick className="w-10 h-10 mx-auto mb-3 opacity-30" />
              <p>No clicks recorded yet</p>
              <p className="text-xs mt-1">Share your link to start tracking clicks</p>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
