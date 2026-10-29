---
name: commit
description: >
  Commit message formatter for this project. Formats [type] Korean title,
  per-file body, and issue linking, then commits. Invoke with /commit.
disable-model-invocation: true
allowed-tools: Bash(git status *) Bash(git diff *) Bash(git log *) Bash(git branch *)
---

# Commit

## Pre-checks

1. Derive the issue number from the current branch name (`feat/12-user-signup` → `#12`). Ask the user only if the
   branch name contains no issue number.
2. Verify the current branch is a work branch (`feat/`, `fix/`, `refactor/`,
   `test/`, `chore/`, `docs/`, `hotfix/`). Never commit directly to `main`
   or `dev`.
3. Exclude Claude working files from the commit (scratch notes, generated reports).

## Message format

```
[type] 제목 (50자 이내, 명사형 종결)

파일명1
- 변경 상세 내용

파일명2
- 변경 상세 내용

Related #이슈번호
```

## Type guide

| Type         | Purpose                               |
|--------------|---------------------------------------|
| `[feat]`     | New feature                           |
| `[fix]`      | Bug fix (also for `hotfix/` branches) |
| `[refactor]` | Refactoring, no behavior change       |
| `[test]`     | Add or update tests                   |
| `[chore]`    | Build, config, tooling                |
| `[docs]`     | Documentation                         |

## Principles

- Detail only the files that carry the main change; omit trivial ones.
- For small changes, the title line alone is enough.
- Split commits by concern — one commit should not mix unrelated changes.

## Committing

1. Stage only the intended files by path (`git add -- <path>...`). Never use `git add -A` or `git add .`.
2. Commit with the formatted message (`git commit -F -` with a heredoc keeps the multi-line body intact).
3. Verify with `git status` and `git log -1 --stat` that only the intended files were committed.
4. Repeat 1–3 for each concern when the changes are split into multiple commits.

## Example

```
[feat] 회원가입 및 이메일 인증 구현

User
- 이메일/비밀번호 형식 검증
- 비밀번호 암호화 저장

AuthService
- 인증 메일 발송 및 인증 코드 검증 로직

Related #12
```
