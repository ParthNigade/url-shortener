import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '@/lib/api';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Link2, ExternalLink, BarChart3, Trash2, Copy, Check, Loader2 } from 'lucide-react';

export default function DashboardPage() {
  const [urls, setUrls] = useState([]);
  const [loading, setLoading] = useState(true);
  const [copiedId, setCopiedId] = useState(null);

  useEffect(() => {
    fetchUrls();
  }, []);

  const fetchUrls = async () => {
    try {
      const res = await api.get('/url/my-urls');
      setUrls(res.data);
    } catch (err) {
      console.error('Failed to fetch URLs:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (shortCode) => {
    if (!window.confirm('Are you sure you want to deactivate this URL?')) return;
    try {
      await api.delete(`/url/${shortCode}`);
      setUrls(urls.filter(u => u.shortCode !== shortCode));
    } catch (err) {
      console.error('Failed to delete:', err);
    }
  };

  const copyToClipboard = (shortUrl, shortCode) => {
    navigator.clipboard.writeText(shortUrl);
    setCopiedId(shortCode);
    setTimeout(() => setCopiedId(null), 2000);
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return '—';
    return new Date(dateStr).toLocaleDateString('en-IN', {
      day: 'numeric', month: 'short', year: 'numeric'
    });
  };

  if (loading) {
    return (
      <div className="min-h-[calc(100vh-4rem)] flex items-center justify-center">
        <Loader2 className="w-8 h-8 animate-spin text-primary" />
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-5xl px-6 py-10">
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">My Links</h1>
          <p className="text-muted-foreground text-sm mt-1">
            {urls.length} link{urls.length !== 1 ? 's' : ''} created
          </p>
        </div>
        <Link to="/">
          <Button className="gap-2">
            <Link2 className="w-4 h-4" />
            Shorten New URL
          </Button>
        </Link>
      </div>

      {urls.length === 0 ? (
        <Card className="border-dashed border-border/50">
          <CardContent className="flex flex-col items-center justify-center py-16">
            <Link2 className="w-12 h-12 text-muted-foreground/30 mb-4" />
            <p className="text-lg font-medium text-muted-foreground">No links yet</p>
            <p className="text-sm text-muted-foreground/70 mb-4">Create your first shortened URL to get started</p>
            <Link to="/">
              <Button>Create your first link</Button>
            </Link>
          </CardContent>
        </Card>
      ) : (
        <div className="space-y-3">
          {urls.map((url) => (
            <Card key={url.shortCode} className="border-border/50 hover:border-primary/30 transition-colors">
              <CardContent className="p-5">
                <div className="flex items-start justify-between gap-4">
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 mb-1">
                      <a
                        href={url.shortUrl}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="text-primary hover:underline font-medium text-sm flex items-center gap-1"
                      >
                        {url.shortUrl}
                        <ExternalLink className="w-3 h-3" />
                      </a>
                      <Button
                        variant="ghost"
                        size="icon"
                        className="h-6 w-6"
                        onClick={() => copyToClipboard(url.shortUrl, url.shortCode)}
                      >
                        {copiedId === url.shortCode ?
                          <Check className="w-3 h-3 text-emerald-400" /> :
                          <Copy className="w-3 h-3" />}
                      </Button>
                    </div>
                    <p className="text-xs text-muted-foreground truncate">{url.originalUrl}</p>
                    <div className="flex items-center gap-3 mt-2 text-xs text-muted-foreground">
                      <span>Created: {formatDate(url.createdAt)}</span>
                      <span>•</span>
                      <span>Expires: {formatDate(url.expiresAt)}</span>
                    </div>
                  </div>

                  <div className="flex items-center gap-2 shrink-0">
                    <Link to={`/analytics/${url.shortCode}`}>
                      <Button variant="outline" size="sm" className="gap-1.5">
                        <BarChart3 className="w-3.5 h-3.5" />
                        Stats
                      </Button>
                    </Link>
                    <Button
                      variant="ghost"
                      size="icon"
                      className="h-8 w-8 text-muted-foreground hover:text-destructive"
                      onClick={() => handleDelete(url.shortCode)}
                    >
                      <Trash2 className="w-4 h-4" />
                    </Button>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
