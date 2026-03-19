import { useState } from 'react';
import { useAuth } from '@/context/AuthContext';
import { useNavigate } from 'react-router-dom';
import api from '@/lib/api';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Link2, Copy, Check, Sparkles, ArrowRight } from 'lucide-react';

export default function HomePage() {
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [url, setUrl] = useState('');
  const [customAlias, setCustomAlias] = useState('');
  const [expiresInDays, setExpiresInDays] = useState('');
  const [result, setResult] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [copied, setCopied] = useState(false);

  const handleShorten = async (e) => {
    e.preventDefault();
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    setError('');
    setResult(null);
    setLoading(true);
    try {
      const payload = { originalUrl: url };
      if (customAlias.trim()) payload.customAlias = customAlias.trim();
      if (expiresInDays) payload.expiresInDays = parseInt(expiresInDays);

      const res = await api.post('/url/shorten', payload);
      setResult(res.data);
      setUrl('');
      setCustomAlias('');
      setExpiresInDays('');
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to shorten URL');
    } finally {
      setLoading(false);
    }
  };

  const copyToClipboard = () => {
    navigator.clipboard.writeText(result.shortUrl);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div className="min-h-[calc(100vh-4rem)] flex flex-col items-center justify-center px-4 py-16">
      {/* Hero */}
      <div className="text-center mb-10 max-w-2xl">
        <div className="inline-flex items-center gap-2 rounded-full bg-primary/10 border border-primary/20 px-4 py-1.5 text-sm text-primary mb-6">
          <Sparkles className="w-3.5 h-3.5" />
          Fast, reliable URL shortening
        </div>
        <h1 className="text-4xl md:text-5xl font-bold tracking-tight mb-4">
          Shorten your links,{' '}
          <span className="bg-gradient-to-r from-primary to-purple-400 bg-clip-text text-transparent">
            amplify your reach
          </span>
        </h1>
        <p className="text-lg text-muted-foreground">
          Create short, memorable links with built-in analytics. Track every click in real time.
        </p>
      </div>

      {/* Shorten Form */}
      <Card className="w-full max-w-2xl border-border/50">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Link2 className="w-5 h-5 text-primary" />
            Shorten a URL
          </CardTitle>
          <CardDescription>Paste your long URL below and get a short link instantly</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleShorten} className="space-y-4">
            {error && (
              <div className="rounded-lg bg-destructive/10 border border-destructive/20 p-3 text-sm text-destructive">
                {error}
              </div>
            )}
            <Input
              type="url"
              placeholder="https://your-very-long-url.com/path/to/page"
              value={url}
              onChange={(e) => setUrl(e.target.value)}
              required
              className="h-12 text-base"
            />
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-1">
                <label className="text-xs text-muted-foreground">Custom Alias (optional)</label>
                <Input
                  type="text"
                  placeholder="my-link"
                  value={customAlias}
                  onChange={(e) => setCustomAlias(e.target.value)}
                />
              </div>
              <div className="space-y-1">
                <label className="text-xs text-muted-foreground">Expires in (days)</label>
                <Input
                  type="number"
                  placeholder="365"
                  value={expiresInDays}
                  onChange={(e) => setExpiresInDays(e.target.value)}
                  min="1"
                />
              </div>
            </div>
            <Button type="submit" className="w-full" size="lg" disabled={loading}>
              {loading ? 'Shortening...' : isAuthenticated ? 'Shorten URL' : 'Sign in to Shorten'}
              <ArrowRight className="w-4 h-4" />
            </Button>
          </form>

          {/* Result */}
          {result && (
            <div className="mt-6 rounded-lg border border-primary/20 bg-primary/5 p-4">
              <p className="text-xs text-muted-foreground mb-2">Your shortened URL</p>
              <div className="flex items-center gap-2">
                <code className="flex-1 rounded-lg bg-background px-3 py-2 text-sm text-primary font-medium border">
                  {result.shortUrl}
                </code>
                <Button
                  variant="outline"
                  size="icon"
                  onClick={copyToClipboard}
                  className="shrink-0"
                >
                  {copied ? <Check className="w-4 h-4 text-emerald-400" /> : <Copy className="w-4 h-4" />}
                </Button>
              </div>
              <p className="text-xs text-muted-foreground mt-2">
                Original: <span className="text-foreground">{result.originalUrl}</span>
              </p>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
