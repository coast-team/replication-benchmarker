/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2013 LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package citi.treedoc;

import java.util.*;

public class TreedocIdFactory implements IdFactory<TreedocId> {

    long baseSiteId = 7;
    long siteId = baseSiteId;		// should be set to a specific value
    int counter = 0;
//	private Random rand;
    static TreedocIdFactory factory;

    public static TreedocIdFactory getFactory() {
        if (factory == null) {
            factory = new TreedocIdFactory();
        }
        return factory;
    }

//	private int newRandom( int l, int h) {
//		int n = h - l + 1;
//		if( n <= 0)
//			n = n-1;
//		return l + rand.nextInt( n);
//	}
    protected TreedocIdFactory() {
//		rand = new Random();
//		rand.setSeed( 10);
    }

    private synchronized TreedocId createNew() {
        TreedocId id = new TreedocId(new TreedocDisambiguator(counter++, siteId));
        return id;
    }

    private TreedocId createLeft(TreedocId t) {
        TreedocId id = new TreedocId(t, new TreedocDisambiguator(counter++, siteId));
        int len = id.bs.length();
        id.bs.clear(len - 1);
        id.bs.set(len);
        return id;
    }

    private TreedocId createRight(TreedocId t) {
        TreedocId id = new TreedocId(t, new TreedocDisambiguator(counter++, siteId));
        int len = id.bs.length();
        id.bs.set(len);
        return id;
    }

    private synchronized List<TreedocId> createNew(int n, TreedocId base) {
        List<TreedocId> l = new ArrayList<TreedocId>();
        TreedocId id = new TreedocId(base, new TreedocDisambiguator(counter++, siteId));
        l.add(id);
        n--;

        outloop:
        for (int level = 1; n > 0; level++) {
            boolean left = true;
            for (int i = 0; i < l.size() && n > 0; i++) {
                TreedocId t = l.get(i);
                if (left) {
                    id = createLeft(t);
                    l.add(i, id);
                    n--;
                    left = false;
                } else {
                    id = createRight(t);
                    i++;
                    l.add(i, id);
                    i++;
                    n--;
                    left = true;
                }
            }
        }
        return l;
    }

    @Override
    public synchronized List<TreedocId> createNew(TreedocId before, TreedocId after, int n, int siteId) {
        if (siteId >= 0) {
            this.siteId = siteId;
        } else {
            this.siteId = baseSiteId;
        }
        if (before == null && after == null) {
            return createNew(n, createNew());
        } else if (before == null) {
            return createNew(n, createLeft(after));
        } else if (after == null) {
            return createNew(n, createRight(before));
        } else {
            return doCreateNew(n, before, after);
        }
    }

    @Override
    public synchronized TreedocId createNew(TreedocId before, TreedocId after, int siteId) {
        if (siteId >= 0) {
            this.siteId = siteId;
        } else {
            this.siteId = baseSiteId;
        }
        if (before == null && after == null) {
            return createNew();
        } else if (before == null) {
            return doCreateNewHead(after);
        } else if (after == null) {
            return doCreateNewTail(before);
        } else {
            return doCreateNew(before, after);
        }
    }

    private boolean smaller(TreedocId p, TreedocId q) {
        int i = 0;
        for (; i < p.bs.length() - 1; i++) {
            if (i >= q.bs.length() - 1) {
                return !p.bs.get(i);
            }
            if (p.bs.get(i) == q.bs.get(i)) {
                continue;
            }
            return !p.bs.get(i);
        }
        if (i == q.bs.length() - 1) {
            return false;
        }
        return q.bs.get(i);
    }

    private TreedocId doCreateNewHead(TreedocId q) {
        TreedocId id = new TreedocId(new TreedocDisambiguator(counter++, siteId));
        if (q.l != null) {
            id.l = new Disambiguators[q.l.length];
            System.arraycopy(q.l, 0, id.l, 0, q.l.length);
        }
        int llen = id.bs.length() - 1;
        int qlen = q.bs.length() - 1;
        id.bs.clear(llen);
        q.bs.clear(qlen);
        id.bs.or(q.bs);
        q.bs.set(qlen);
        int idlen = llen;
        if (idlen < qlen) {
            idlen = qlen;
        }
        id.bs.set(idlen + 1);
        return id;
    }

    private TreedocId doCreateNewTail(TreedocId p) {
        TreedocId id = new TreedocId(new TreedocDisambiguator(counter++, siteId));
        if (p.l != null) {
            id.l = new Disambiguators[p.l.length];
            System.arraycopy(p.l, 0, id.l, 0, p.l.length);
        }
        id.bs.or(p.bs);
        id.bs.set(id.bs.length());
        return id;
    }

    private TreedocId doCreateNew(TreedocId p, TreedocId q) {
//		boolean ok = true;
//		if( counter > 90) {
//			ok = true;
//		}
        TreedocId m = doCreateNew0(p, q);
        if (!smaller(p, m)) {
            throw new RuntimeException("create error");
//			ok = false;
//			smaller(p,m);
        }
        if (!smaller(m, q)) {
            throw new RuntimeException("create error");
//			ok = false;
//			smaller(m,q);
        }
        if (m.bs.length() == 0) {
            throw new RuntimeException("create error");
//			ok = false;
//			smaller(m,q);
        }
        //	if( ! ok) {
        //		counter--;
        //		m = doCreateNew0( p, q);
        //	}
        return m;
    }

    private TreedocId doCreateNew0(TreedocId p, TreedocId q) {
        int i = 0;
        int lenP = p.bs.length() - 1;
        int lenQ = q.bs.length() - 1;
        TreedocId r = new TreedocId(new TreedocDisambiguator(counter++, siteId));
        for (i = 0; i < lenP && i < lenQ; i++) {
            if (p.bs.get(i) != q.bs.get(i)) {
                r.bs.set(i);
                return r;
            }
            r.bs.set(i, p.bs.get(i));
        }
        if (lenP == lenQ + 1) {
            r.bs.set(i, p.bs.get(i));
            i++;
            r.bs.set(i);
            r.bs.set(i + 1);
            return r;
        }
        if (lenP + 1 == lenQ) {
            r.bs.set(i, q.bs.get(i));
            i++;
            r.bs.clear(i);
            r.bs.set(i + 1);
            return r;
        }
        if (lenP > lenQ + 1) {
            for (;;) {
                r.bs.set(i, p.bs.get(i));
                i++;
                if (i == lenP) {
                    break;
                }
                if (p.bs.get(i) == false) {
                    break;
                }
            }
            if (i == lenP) {
                r.bs.set(i++);
            }
            r.bs.set(i);
            return r;
        }
        if (lenP + 1 < lenQ) {
            for (;;) {
                r.bs.set(i, q.bs.get(i));
                i++;
                if (i == lenQ) {
                    break;
                }
                if (q.bs.get(i) == true) {
                    break;
                }
            }
            if (i == lenQ) {
                r.bs.clear(i++);
            }
            r.bs.set(i);
            return r;
        }
        System.out.println(p);
        System.out.println(q);
        System.out.println(r);
        throw new RuntimeException("not expected:" + lenP + ":" + lenQ);
    }

    private List<TreedocId> doCreateNew(int n, TreedocId p, TreedocId q) {
        boolean ok = true;
        List<TreedocId> m = doCreateNew0(n, p, q);
        if (!smaller(p, m.get(0))) {
            throw new RuntimeException("create error");
//			ok = false;
//			smaller( p, m.get( 0));
        }

        for (int i = 1; i < m.size(); i++) {
            if (!smaller(m.get(i - 1), m.get(i))) {
                throw new RuntimeException("create error");
//				ok = false;
//				smaller(p,m);
            }
        }

        if (!smaller(m.get(m.size() - 1), q)) {
//			throw new RuntimeException( "create error");
            ok = false;
            smaller(m.get(m.size() - 1), q);
        }
        if (!ok) {
            counter--;
            m = doCreateNew0(n, p, q);
        }
        return m;
    }

    private List<TreedocId> doCreateNew0(int n, TreedocId p, TreedocId q) {
        int i = 0;
        int lenP = p.bs.length() - 1;
        int lenQ = q.bs.length() - 1;
        for (i = 0; i < lenP && i < lenQ; i++) {
            if (p.bs.get(i) != q.bs.get(i)) {
                if (lenP < lenQ) {
                    return createNew(n, createRight(p));
                } else {
                    return createNew(n, createLeft(q));
                }
            }
        }
        if (lenP == lenQ + 1) {
            return createNew(n, createRight(p));
        }
        if (lenP + 1 == lenQ) {
            return createNew(n, createLeft(q));
        }
        if (lenP > lenQ + 1) {
            TreedocId id = createLeft(q);
            while (!smaller(p, id)) {
                id = createRight(id);
            }
            id = createRight(id);
            return createNew(n, id);
        }
        if (lenP + 1 < lenQ) {
            TreedocId id = createRight(p);
            while (!smaller(id, q)) {
                id = createLeft(id);
            }
            id = createLeft(id);
            return createNew(n, id);
        }
        System.out.println(p);
        System.out.println(q);
        throw new RuntimeException("not expected:" + lenP + ":" + lenQ);
    }
}
