import fs from 'node:fs';
import crypto from 'node:crypto';
const seal=JSON.parse(fs.readFileSync(new URL('../creator-seal.json',import.meta.url),'utf8'));
const digest=crypto.createHash('sha256').update(seal.credit,'utf8').digest('hex');
const html=fs.readFileSync(new URL('../server/public/index.html',import.meta.url),'utf8');
const resources=fs.readFileSync(new URL('../android/app/src/main/res/values/strings.xml',import.meta.url),'utf8');
if(digest!==seal.sha256||!html.includes('<!-- '+seal.credit+' -->')||!resources.includes(seal.credit)||!resources.includes(seal.sha256)){console.error('Creator attribution integrity check failed.');process.exit(1)}
console.log('Creator attribution seal verified.');
