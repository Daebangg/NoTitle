---
name: issue
description: >
  GitHub issue writer for this project. Formats issues in the
  problem/introduction style from a business perspective and creates them
  with gh. Invoke with /issue.
disable-model-invocation: true
allowed-tools: Bash(gh issue list *) Bash(gh issue view *) Bash(gh label list *)
---

# Issue

## Core principles

- Write from a **business perspective**. Never include implementation details such as file names, class names, phase
  numbers, or internal jargon.
- Describe features from the user's point of view.
- Write introduction items as checkboxes — they are verified at PR time.
- Issue titles and body are written in Korean.

## Issue format

```
제목: [간결한 작업 목적] (50자 이내)

## 문제

[현재 상황의 문제점 또는 필요성 1~2문장]

## 도입

- [ ] [비즈니스 관점의 작업 항목 1]
- [ ] [비즈니스 관점의 작업 항목 2]
```

## Creating the issue

Attach a **type label** matching the work type and an assignee:

```bash
gh issue create --title "..." --body "..." --label "<type>" --assignee @me
```

Check `gh label list` first and use only labels that exist in the repository.

## After creation

Offer to create the work branch from the new issue number, following the branch convention: `type/이슈번호-desc`, branched
from `dev`.

## Example

```
제목: 회원가입 및 로그인 기능 구현

## 문제

사용자가 계정을 만들고 본인임을 인증할 방법이 없음

## 도입

- [ ] 이메일로 회원가입
- [ ] 이메일 인증을 통한 계정 활성화
- [ ] 로그인 및 로그인 상태 유지
- [ ] 비밀번호 재설정
```
